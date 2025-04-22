package com.hqing.hqingrpcspringbootstarter.annotation;

import com.hqing.hqingrpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.hqing.hqingrpcspringbootstarter.bootstrap.RpcServiceBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用Rpc注解
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcServiceBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动server
     */
    boolean needServer() default true;

    /**
     * 扫描包路径
     */
    String[] basePackages() default {};
}
