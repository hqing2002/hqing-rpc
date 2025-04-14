package com.hqing.hqrpc.proxy;

import com.hqing.hqrpc.RpcApplication;

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
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }

        return serviceClass.cast(Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()));
    }

    /**
     * 根据服务类获取Mock代理对象
     */
    private static <T> T getMockProxy(Class<T> serviceClass) {
        return serviceClass.cast(Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()));
    }
}
