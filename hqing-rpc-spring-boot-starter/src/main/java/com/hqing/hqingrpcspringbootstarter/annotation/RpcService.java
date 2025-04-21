package com.hqing.hqingrpcspringbootstarter.annotation;

import com.hqing.hqrpc.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者注解(用于注册服务)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 指定该服务实现了哪个接口
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本号
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
