package com.hqing.hqrpc.bootstrap;

import cn.hutool.core.net.NetUtil;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.model.ServiceRegisterInfo;
import com.hqing.hqrpc.registry.LocalRegistry;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryFactory;
import com.hqing.hqrpc.server.ServerFactory;
import com.hqing.hqrpc.server.VertxServer;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务提供者初始化
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ProviderBootstrap {
    /**
     * 使用原子类保证只开启一次心跳检测
     */
    private static final AtomicBoolean FIRST_START_HEAT_BEAT = new AtomicBoolean(true);

    /**
     * 初始化
     *
     * @param serviceRegisterInfoList 服务注册列表
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        //Rpc框架初始化, 获取Rpc全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            //获取注册服务信息
            String serviceName = serviceRegisterInfo.getServiceName();
            Class<?> implClass = serviceRegisterInfo.getImplClass();
            String serviceVersion = serviceRegisterInfo.getServiceVersion();

            //本地注册
            LocalRegistry.register(serviceName, implClass);

            //获取注册中心配置
            RegistryConfig registryConfig = rpcConfig.getRegistry();
            //获取注册中心实例
            Registry registry = RegistryFactory.getInstance(registryConfig.getName());
            //开启心跳检测
            if (FIRST_START_HEAT_BEAT.get()) {
                registry.heartBeat();
                FIRST_START_HEAT_BEAT.set(false);
            }
            //构造服务注册元数据
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            //获取当前IP地址
            InetAddress localhost = NetUtil.getLocalhost();
            serviceMetaInfo.setServiceHost(localhost.getHostAddress());
            serviceMetaInfo.setServicePort(rpcConfig.getProtocol().getPort());
            serviceMetaInfo.setServiceVersion(serviceVersion);

            //服务注册
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败", e);
            }

            //启动web服务器, 提供服务
            VertxServer vertxServer = ServerFactory.getVertxServer(rpcConfig.getProtocol().getName());
            vertxServer.doStart(rpcConfig.getProtocol().getPort());
        }
    }
}
