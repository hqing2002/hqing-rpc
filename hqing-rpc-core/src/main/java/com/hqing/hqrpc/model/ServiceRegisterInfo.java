package com.hqing.hqrpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务信息注册类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRegisterInfo<T> {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = "1.0";

    /**
     * 实现类
     */
    private Class<? extends T> implClass;

    public ServiceRegisterInfo(String serviceName, Class<? extends T> implClass) {
        this.serviceName = serviceName;
        this.implClass = implClass;
    }
}
