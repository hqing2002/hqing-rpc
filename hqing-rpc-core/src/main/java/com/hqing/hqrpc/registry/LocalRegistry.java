package com.hqing.hqrpc.registry;

import com.hqing.hqrpc.model.ServiceLocalRegisterInfo;

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
    private static final Map<String, ServiceLocalRegisterInfo<?>> SERVICE_MAP = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName              服务名称
     * @param serviceLocalRegisterInfo 本地服务注册信息
     */
    public static void register(String serviceName, ServiceLocalRegisterInfo<?> serviceLocalRegisterInfo) {
        SERVICE_MAP.put(serviceName, serviceLocalRegisterInfo);
    }

    /**
     * 获取服务
     *
     * @param serviceName 服务名称
     * @return 服务实现类
     */
    public static ServiceLocalRegisterInfo<?> get(String serviceName) {
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
