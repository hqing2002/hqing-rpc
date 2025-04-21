package com.hqing.hqingrpcspringbootstarter.bootstrap;

import com.hqing.hqingrpcspringbootstarter.annotation.RpcReference;
import com.hqing.hqrpc.proxy.ServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * RPC 注入代理对象
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {
    /**
     * Bean初始化后执行, 扫描并注入代理对象
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //获得bean的class对象
        Class<?> beanClass = bean.getClass();
        //获取该bean对象的所有属性字段
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //判断该属性字段有没有@RpcReference注解
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            //有注解说明需要注入代理对象
            if (rpcReference != null) {
                //获取注解中需要注入的接口类型和版本号
                Class<?> interfaceClass = rpcReference.interfaceClass();
                String serviceVersion = rpcReference.serviceVersion();
                //如果没有指定类型就直接获取字段类型
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                //设置强制访问该属性字段
                field.setAccessible(true);
                //调用服务代理工厂获取代理对象
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass, serviceVersion);
                try {
                    //设置修改bean对象的field字段, 设置新值为代理对象proxyObject
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                } catch (Exception e) {
                    throw new RuntimeException(field.getName() + "字段注入代理对象失败", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
