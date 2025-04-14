package com.hqing.hqrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务注册器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class LocalRegistry {
    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> SERVICE_MAP = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName      服务名称
     * @param serviceImplClass 服务实现类
     */
    public static void register(String serviceName, Class<?> serviceImplClass) {
        SERVICE_MAP.put(serviceName, serviceImplClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName 服务名称
     * @return 服务实现类
     */
    public static Class<?> get(String serviceName) {
        return SERVICE_MAP.get(serviceName);
    }

    /**
     * 删除服务
     *
     * @param serviceName 服务名称
     */
    public static void remove(String serviceName) {
        SERVICE_MAP.remove(serviceName);
    }
}
