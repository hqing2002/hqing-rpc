package com.hqing.exampleprovider;

import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.registry.LocalRegistry;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryFactory;
import com.hqing.hqrpc.server.HttpServer;
import com.hqing.hqrpc.server.VertxHttpServer;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务提供者启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class ExampleProviderApplication {
    public static void main(String[] args) {
        //注册到服务注册器
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getSeverHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            log.error("服务注册失败", e);
            throw new RuntimeException(e);
        }
        //启动web服务器, 提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
