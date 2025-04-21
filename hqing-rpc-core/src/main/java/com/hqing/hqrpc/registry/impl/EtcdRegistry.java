package com.hqing.hqrpc.registry.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryServiceCache;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Etcd注册中心
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class EtcdRegistry implements Registry {
    /**
     * 根节点(服务端)
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";
    /**
     * 服务存活时间(服务端)
     */
    private static final Long TTL = 30L;
    /**
     * 续签间隔(服务端)
     */
    private static final Integer RENEWAL_TIME = 10;

    /**
     * 本机注册节点key集合, 用于维护续期(服务端)
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();
    /**
     * 注册中心服务缓存(消费端)
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    /**
     * 正在监听的服务集合(消费端)
     */
    private final Set<String> watchingServiceSet = new ConcurrentHashSet<>();
    /**
     * 超时时长(服务端,消费端)
     */
    private Long timeout;
    /**
     * ETCD客户端
     */
    private Client etcdClient;
    /**
     * ETCD键值对操作客户端
     */
    private KV kvClient;

    /**
     * 注册中心初始化
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        String address = registryConfig.getAddress();
        timeout = registryConfig.getTimeout();
        try {
            //连接ETCD注册中心
            etcdClient = Client.builder().endpoints(address).connectTimeout(Duration.ofMillis(timeout)).build();
            //测试连接情况,显式触发连接
            etcdClient.getClusterClient().listMember().get(timeout, RpcConstant.DEFAULT_TIME_UNIT);
            //获取KV客户端
            kvClient = etcdClient.getKVClient();
        } catch (Exception e) {
            log.error("ETCD注册中心连接失败", e.getCause());
            destroy();
            throw new RuntimeException("ETCD注册中心连接错误");
        }
    }

    /**
     * 服务注册
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //获取租约客户端
        Lease leaseClient = etcdClient.getLeaseClient();

        //创建一个30s的租约
        long leaseId = leaseClient.grant(TTL).get(timeout, RpcConstant.DEFAULT_TIME_UNIT).getID();

        //设置要存储的键值对
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        //K: /rpc/serviceName:serviceVersion/serviceHost:servicePort
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        //V: ServiceMetaInfo->JSON
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对和租约关联起来, 并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get(timeout, RpcConstant.DEFAULT_TIME_UNIT);

        //添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registryKey);
    }

    /**
     * 服务注销
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String key = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get(timeout, RpcConstant.DEFAULT_TIME_UNIT);
        //删除节点缓存
        localRegisterNodeKeySet.remove(key);
    }

    /**
     * 服务发现
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //从缓存中获取服务
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache(serviceKey);
        if (CollUtil.isNotEmpty(cacheServiceMetaInfoList)) {
            return cacheServiceMetaInfoList;
        }
        //判断服务是否被监听了, 如果被监听了就不用添加监听
        if (!registryServiceCache.containsService(serviceKey)) {
            watch(serviceKey);
        }
        //前缀搜索, 后面一定要跟上'/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            //开启使用前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            //获取服务信息列表
            CompletableFuture<GetResponse> completableFuture = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption);
            List<KeyValue> keyValues = completableFuture.get(timeout, RpcConstant.DEFAULT_TIME_UNIT).getKvs();

            //解析服务信息
            return keyValues.stream().map(keyValue -> {
                //获取服务节点键
                String serviceNodeKey = keyValue.getKey().toString(StandardCharsets.UTF_8);
                //获取服务节点内存储的元信息
                String serviceNodeValue = keyValue.getValue().toString(StandardCharsets.UTF_8);
                ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(serviceNodeValue, ServiceMetaInfo.class);
                //将节点信息写入缓存
                registryServiceCache.writeCache(serviceKey, serviceNodeKey, serviceMetaInfo);
                return serviceMetaInfo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    /**
     * 注册中心销毁
     */
    @Override
    public void destroy() {
        log.info("当前节点下线");

        //注销该服务注册的所有key
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get(timeout, RpcConstant.DEFAULT_TIME_UNIT);
            } catch (Exception e) {
                log.error("{} 节点下线失败", key, e);
                throw new RuntimeException(e);
            }
        }

        if (kvClient != null) {
            kvClient.close();
        }
        if (etcdClient != null) {
            etcdClient.close();
        }
    }

    /**
     * 心跳检测
     */
    @Override
    public void heartBeat() {
        //间隔10s心跳续签一次
        CronUtil.schedule(String.format("*/%s * * * * *", RENEWAL_TIME), (Task) () -> {
            //遍历所有注册的key
            for (String key : localRegisterNodeKeySet) {
                try {
                    //获取key的值
                    List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                            .get(timeout, RpcConstant.DEFAULT_TIME_UNIT).getKvs();

                    //节点过期(说明服务宕机需要重启节点)
                    if (CollUtil.isEmpty(keyValues)) {
                        continue;
                    }
                    //服务未过期重新注册续签
                    KeyValue keyValue = keyValues.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    log.error("服务续签失败", e);
                    throw new RuntimeException(key + "续签失败", e);
                }
            }
        });
        //支持秒级定时任务
        CronUtil.setMatchSecond(true);
        //开启任务
        CronUtil.start();
    }

    /**
     * 监听服务
     */
    @Override
    public void watch(String serviceKey) {
        //判断当前服务是否在监听集合里面, 不在就添加监听
        boolean serviceCacheNotExist = watchingServiceSet.add(serviceKey);
        if (serviceCacheNotExist) {
            //设置为前缀监听
            Watch watchClient = etcdClient.getWatchClient();
            WatchOption watchOption = WatchOption.builder().isPrefix(true).build();
            //监听服务
            watchClient.watch(ByteSequence.from(ETCD_ROOT_PATH + serviceKey, StandardCharsets.UTF_8), watchOption, response -> {
                //遍历变化的节点
                for (WatchEvent event : response.getEvents()) {
                    //变化节点的键值对
                    KeyValue keyValue = event.getKeyValue();
                    //获取变化节点的key
                    String serviceNodeKey = keyValue.getKey().toString(StandardCharsets.UTF_8);
                    //获取节点改变的类型
                    WatchEvent.EventType eventType = event.getEventType();
                    //节点被移除了
                    if (eventType.equals(WatchEvent.EventType.DELETE)) {
                        //把该节点从缓存中删除
                        registryServiceCache.removeNodeCache(serviceKey, serviceNodeKey);
                    }
                    //新增节点
                    if (eventType.equals(WatchEvent.EventType.PUT)) {
                        //判断是不是心跳续约的节点
                        if (registryServiceCache.containsServiceNode(serviceKey, serviceNodeKey)) {
                            //如果节点已经在缓存中存在了说明不是新增的直接break
                            break;
                        }
                        //在缓存中新增节点, 获取变化节点的value转为服务元信息, 存入缓存中
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        registryServiceCache.writeCache(serviceKey, serviceNodeKey, serviceMetaInfo);
                    }
                }
            });
        }
    }
}
