package com.hqing.hqrpc.proxy;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock服务代理(JDK动态代理)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return BeanUtil.toBean(new Object(), returnType);
    }
}
