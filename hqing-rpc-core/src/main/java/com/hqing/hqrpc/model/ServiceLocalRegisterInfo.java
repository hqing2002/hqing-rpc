package com.hqing.hqrpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 服务本地注册
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Data
@AllArgsConstructor
public class ServiceLocalRegisterInfo<T> {
    /**
     * 实现类class
     */
    private Class<? extends T> serviceImplClass;

    /**
     * 实现类实例对象
     */
    private T instance;
}
