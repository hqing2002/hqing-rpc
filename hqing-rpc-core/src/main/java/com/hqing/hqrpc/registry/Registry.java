package com.hqing.hqrpc.registry;

import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心接口
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface Registry {
    /**
     * 注册中心初始化
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务(服务端)
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务(服务端)
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务发现(获取某服务的所有节点, 消费端)
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 注册中心销毁
     */
    void destroy();

    /**
     * 心跳检测(服务端)
     */
    void heartBeat();
}
