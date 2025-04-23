package com.hqing.hqrpc.bootstrap;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.model.ServiceLocalRegisterInfo;
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
     * 使用原子类保证只执行一次
     */
    private static final AtomicBoolean FIRST_INIT = new AtomicBoolean(true);

    /**
     * 服务提供者初始化
     */
    public static void init() {
        //Rpc框架初始化, 获取Rpc全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //获取注册中心配置
        RegistryConfig registryConfig = rpcConfig.getRegistry();
        //获取注册中心实例
        Registry registry = RegistryFactory.getInstance(registryConfig.getName());
        //确保只执行一次心跳检测和服务器启动
        if (FIRST_INIT.get()) {
            //开启心跳检测
            registry.heartBeat();
            //启动web服务器, 提供服务
            VertxServer vertxServer = ServerFactory.getVertxServer(rpcConfig.getProtocol().getName());
            vertxServer.doStart(rpcConfig.getProtocol().getPort());
            FIRST_INIT.set(false);
        }
    }

    /**
     * 服务注册
     *
     * @param serviceRegisterInfoList 注册服务信息列表
     */
    public static void registerService(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        //待注册列表为空, 直接退出
        if (CollUtil.isEmpty(serviceRegisterInfoList)) {
            return;
        }

        //Rpc框架初始化, 获取Rpc全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        //获取注册中心配置
        RegistryConfig registryConfig = rpcConfig.getRegistry();
        //获取注册中心实例
        Registry registry = RegistryFactory.getInstance(registryConfig.getName());

        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            //获取注册服务信息
            String serviceName = serviceRegisterInfo.getServiceName();
            ServiceLocalRegisterInfo<?> serviceLocalRegisterInfo = serviceRegisterInfo.getServiceLocalRegisterInfo();
            String serviceVersion = serviceRegisterInfo.getServiceVersion();

            //本地注册
            LocalRegistry.register(serviceName, serviceLocalRegisterInfo);

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
        }
    }
}
