package com.hqing.hqingrpcspringbootstarter.bootstrap;

import com.hqing.hqingrpcspringbootstarter.annotation.EnableRpc;
import com.hqing.hqrpc.bootstrap.ConsumerBootstrap;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

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
        //获取 EnableRpc 注解的值
        Map<String, Object> enableRpcAttributesMap = importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName());
        boolean needServer = true;
        if (enableRpcAttributesMap != null) {
            needServer = (boolean) enableRpcAttributesMap.get("needServer");
        }
        //根据参数判断初始化类型
        if (needServer) {
            ProviderBootstrap.init();
        } else {
            ConsumerBootstrap.init();
        }
    }
}
