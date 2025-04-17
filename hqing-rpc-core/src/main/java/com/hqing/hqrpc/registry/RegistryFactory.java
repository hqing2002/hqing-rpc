package com.hqing.hqrpc.registry;

import com.hqing.hqrpc.registry.impl.EtcdRegistry;
import com.hqing.hqrpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心工厂(用于获取注册中心对象)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class RegistryFactory {
    /**
     * 默认使用ETCD注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 获取注册中心实例
     */
    public static Registry getInstance(String key) {
        Registry registry;
        log.info("加载注册中心: {}", key);
        try {
            registry = SpiLoader.getInstance(Registry.class, key);
        } catch (Exception e) {
            log.error("注册中心加载失败, 使用默认注册中心{}", DEFAULT_REGISTRY.getClass().getName());
            registry = DEFAULT_REGISTRY;
        }
        return registry;
    }
}
