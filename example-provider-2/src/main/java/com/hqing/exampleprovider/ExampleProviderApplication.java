package com.hqing.exampleprovider;

import cn.hutool.core.net.NetUtil;
import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.registry.LocalRegistry;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryFactory;
import com.hqing.hqrpc.server.ServerFactory;
import com.hqing.hqrpc.server.VertxServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

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
        RegistryConfig registryConfig = rpcConfig.getRegistry();
        Registry registry = RegistryFactory.getInstance(registryConfig.getName());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        InetAddress localhost = NetUtil.getLocalhost();
        serviceMetaInfo.setServiceHost(localhost.getHostAddress());
        serviceMetaInfo.setServicePort(rpcConfig.getProtocol().getPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            log.error("服务注册失败", e);
            throw new RuntimeException(e);
        }
        //启动web服务器, 提供服务
        VertxServer vertxServer = ServerFactory.getVertxServer(rpcConfig.getProtocol().getName());
        vertxServer.doStart(RpcApplication.getRpcConfig().getProtocol().getPort());
    }
}
