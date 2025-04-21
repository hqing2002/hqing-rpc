package com.hqing.hqingrpcspringbootstarter.bootstrap;

import com.hqing.hqingrpcspringbootstarter.annotation.RpcService;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import com.hqing.hqrpc.model.ServiceRegisterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * RPC 服务提供
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
    /**
     * Bean初始化后执行, 扫描并进行服务注册
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //获得Bean的class对象
        Class<?> beanClass = bean.getClass();
        //判断该Bean有没有使用@RpcService注解
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        //如果有@RpcService注解注解说明该类被标注为接口实现类
        if (rpcService != null) {
            //获取注解参数
            Class<?> interfaceClass = rpcService.interfaceClass();
            String serviceVersion = rpcService.serviceVersion();
            //如果使用注解时没有指定实现了哪个接口
            if (interfaceClass == void.class) {
                //就设置为该类实现的第一个接口
                interfaceClass = beanClass.getInterfaces()[0];
            }
            //获取实现的接口名称
            String serviceName = interfaceClass.getName();
            //构造注册服务信息
            List<ServiceRegisterInfo<?>> registerInfoList = new ArrayList<>();
            ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>
                    (serviceName, serviceVersion, beanClass);
            //将信息添加到列表中
            registerInfoList.add(serviceRegisterInfo);
            //调用ProviderBootstrap进行服务注册
            ProviderBootstrap.registerService(registerInfoList);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
