package com.hqing.hqingrpcspringbootstarter.annotation;

import com.hqing.hqrpc.constant.RpcConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费者注解(用于注入服务)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    /**
     * 需要代理的服务接口
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本号
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
