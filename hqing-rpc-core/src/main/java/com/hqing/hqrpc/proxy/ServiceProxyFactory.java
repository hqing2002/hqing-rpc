package com.hqing.hqrpc.proxy;

import com.hqing.hqrpc.RpcApplication;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂(用于创建代理对象)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class ServiceProxyFactory {
    /**
     * 根据服务类获取代理对象
     */
    public static <T> T getProxy(Class<T> serviceClass, String serviceVersion) {
        if (RpcApplication.getRpcConfig().getConsumer().isMock()) {
            log.info("开启MOCK调用模式");
            return getMockProxy(serviceClass);
        }

        return serviceClass.cast(Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy(serviceVersion)));
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
