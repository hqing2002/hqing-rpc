package com.hqing.hqrpc.registry;

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
import java.util.List;
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
        long leaseId = leaseClient.grant(30).get(timeout, RpcConstant.DEFAULT_TIME_UNIT).getID();

        //设置要存储的键值对
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对和租约关联起来, 并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get(timeout, RpcConstant.DEFAULT_TIME_UNIT);
    }

    /**
     * 服务注销
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String key = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get(timeout, RpcConstant.DEFAULT_TIME_UNIT);
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
     * 服务销毁
     */
    @Override
    public void destroy() {
        log.info("当前节点下线");
        if (kvClient != null) {
            kvClient.close();
        }
        if (etcdClient != null) {
            etcdClient.close();
        }
    }
}
