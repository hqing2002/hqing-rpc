package com.hqing.hqingrpcspringbootstarter.bootstrap;

import com.hqing.hqingrpcspringbootstarter.annotation.RpcReference;
import com.hqing.hqingrpcspringbootstarter.annotation.RpcService;
import com.hqing.hqingrpcspringbootstarter.model.PackageHolder;
import com.hqing.hqingrpcspringbootstarter.utils.AnnotationScannerUtil;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import com.hqing.hqrpc.model.ServiceLocalRegisterInfo;
import com.hqing.hqrpc.model.ServiceRegisterInfo;
import com.hqing.hqrpc.proxy.ServiceProxyFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RPC框架包扫描, 完成服务注册和代理对象注入
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class RpcServiceBootstrap implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * 注入包路径对象
     */
    @Resource
    PackageHolder packageHolder;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //确保在根节点上下文执行
        if (event.getApplicationContext().getParent() == null) {
            //获取包下的所有带注解的类
            Map<Class<?>, AnnotationScannerUtil.AnnotatedInfo> classWithAnnotationMap = AnnotationScannerUtil
                    .scanPackageWithAnnotation(packageHolder.getBasePackages(), RpcService.class, RpcReference.class);
            Set<Class<?>> classes = classWithAnnotationMap.keySet();
            //遍历所有带注解的类, 先进行服务代理注入
            for (Class<?> beanClass : classes) {
                AnnotationScannerUtil.AnnotatedInfo annotatedInfo = classWithAnnotationMap.get(beanClass);
                //如果字段注解不为空, 执行代理对象注入
                List<Field> fieldsWithAnnotation = annotatedInfo.getFieldsWithAnnotation();
                if (!fieldsWithAnnotation.isEmpty()) {
                    proxyInjector(applicationContext.getBean(beanClass), fieldsWithAnnotation);
                }
            }
            //再次遍历带注解的类, 再进行服务注册
            for (Class<?> beanClass : classes) {
                //获取注解
                AnnotationScannerUtil.AnnotatedInfo annotatedInfo = classWithAnnotationMap.get(beanClass);
                //如果包含类注解, 执行服务注册
                if (annotatedInfo.isHasClassAnnotation()) {
                    registerService(beanClass, applicationContext.getBean(beanClass));
                }
            }
        }
    }

    /**
     * 对提供了@RpcService注解的类进行服务注册
     */
    private void registerService(Class<?> beanClass, Object instance) {
        //获取该类的@RpcService注解
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
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
                (serviceName, serviceVersion, new ServiceLocalRegisterInfo<>(beanClass, instance));
        //将信息添加到列表中
        registerInfoList.add(serviceRegisterInfo);
        //调用ProviderBootstrap进行服务注册
        ProviderBootstrap.registerService(registerInfoList);
    }

    /**
     * 对提供了@RpcReference字段进行代理注入
     */
    private void proxyInjector(Object bean, List<Field> fieldList) {
        for (Field field : fieldList) {
            //获取该属性字段的@RpcReference注解
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
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
}
