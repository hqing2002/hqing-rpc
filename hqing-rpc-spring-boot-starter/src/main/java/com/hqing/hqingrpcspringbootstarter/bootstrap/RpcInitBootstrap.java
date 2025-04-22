package com.hqing.hqingrpcspringbootstarter.bootstrap;

import cn.hutool.core.util.StrUtil;
import com.hqing.hqingrpcspringbootstarter.annotation.EnableRpc;
import com.hqing.hqingrpcspringbootstarter.model.PackageHolder;
import com.hqing.hqrpc.bootstrap.ConsumerBootstrap;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * RPC 框架初始化
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    /**
     * Spring 在使用@Import导入该类时会执行registerBeanDefinitions方法
     *
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry               current bean definition registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取 @EnableRpc 注解定义的属性
        Map<String, Object> enableRpcAttributesMap = importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName());
        if (enableRpcAttributesMap == null) {
            throw new RuntimeException("RPC框架初始化异常");
        }
        //获取需要扫描的包名
        String[] basePackage = getPackagesToScan(importingClassMetadata);
        //将PackageHolder注册为SpringBean
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PackageHolder.class);
        //使用构造器注入包名
        builder.addConstructorArgValue(basePackage);
        registry.registerBeanDefinition("packageHolder", builder.getBeanDefinition());

        //判断是否需要加载rpc服务器
        boolean needServer = (boolean) enableRpcAttributesMap.get("needServer");
        if (needServer) {
            //使用服务提供者的方式初始化框架
            ProviderBootstrap.init();
        } else {
            //使用服务消费者的方式初始化框架
            ConsumerBootstrap.init();
        }
    }

    /**
     * 获取扫描包路径
     */
    private String[] getPackagesToScan(AnnotationMetadata importingClassMetadata) {
        //获取注解中定义的扫描路径
        Map<String, Object> enableRpcAttributesMap = importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName());
        String[] basePackages = new String[]{};
        if (enableRpcAttributesMap != null) {
            basePackages = (String[]) enableRpcAttributesMap.get("basePackages");
        }
        //将扫描路径放入set集合中降重
        Set<String> resultPackages = new HashSet<>();
        for (String basePackage : basePackages) {
            if (StrUtil.isNotBlank(basePackage)) {
                resultPackages.add(basePackage);
            }
        }
        //如果注解没有传参就使用启动类的包名
        if (resultPackages.isEmpty()) {
            //获取使用注解@EnbaleRpc类的全类名
            String className = importingClassMetadata.getClassName();
            try {
                //加载该类的class对象
                Class<?> applicationClass = Class.forName(className);
                //获取类所在的包名
                String packageName = applicationClass.getPackageName();
                resultPackages.add(packageName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        //将set集合转为数组
        return resultPackages.toArray(new String[0]);
    }
}
