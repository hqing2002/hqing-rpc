package com.hqing.hqrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂(用于创建代理对象)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return serviceClass.cast(Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()));
    }
}
