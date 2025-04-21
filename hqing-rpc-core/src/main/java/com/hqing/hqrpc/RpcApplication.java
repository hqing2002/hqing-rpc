package com.hqing.hqrpc;

import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryFactory;
import com.hqing.hqrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用, 存放项目全局配置, 双检查锁单例模式
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化, 支持传入自定义配置
     *
     * @param newRpcConfig 自定义配置
     */
    private static void init(RpcConfig newRpcConfig) {
        //传入空配置就用默认的
        if (newRpcConfig == null) {
            newRpcConfig = new RpcConfig();
        }
        rpcConfig = newRpcConfig;
        log.info("RPC初始化配置: {}", newRpcConfig);

        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistry();
        Registry registry = RegistryFactory.getInstance(registryConfig.getName());
        registry.init(registryConfig);
        log.info("注册中心初始化配置: {}", registryConfig);

        //创建并注册 Shutdown Hook JVM主动退出时执行的操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化, 从配置文件中读取Rpc配置
     */
    private static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            //加载失败, 使用默认配置
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取Rpc配置
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
