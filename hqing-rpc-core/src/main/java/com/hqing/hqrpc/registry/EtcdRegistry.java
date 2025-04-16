package com.hqing.hqrpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
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
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";
    /**
     * 服务存活时间
     */
    private static final Long TTL = 30L;
    /**
     * 续签间隔
     */
    private static final Integer RENEWAL_TIME = 10;
    /**
     * 本机注册节点key集合(用于维护续期)
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();
    /**
     * ETCD客户端
     */
    private Client etcdClient;
    /**
     * ETCD键值对操作客户端
     */
    private KV kvClient;
    /**
     * 超时时长
     */
    private Long timeout;

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
            //开启心跳检测
            heartBeat();
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
        //前缀搜索, 后面一定要跟上'/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            //根据前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            CompletableFuture<GetResponse> completableFuture = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption);
            List<KeyValue> keyValues = completableFuture.get(timeout, RpcConstant.DEFAULT_TIME_UNIT).getKvs();
            //解析服务信息
            return keyValues.stream().map(keyValue -> {
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
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
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
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
            //遍历所以注册的key
            for (String key : localRegisterNodeKeySet) {
                try {
                    //获取key的值
                    List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();

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
}
