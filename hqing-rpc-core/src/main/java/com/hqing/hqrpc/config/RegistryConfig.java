package com.hqing.hqrpc.config;

import com.hqing.hqrpc.registry.RegistryKeys;
import lombok.Data;

/**
 * RPC框架注册中心配置
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry = RegistryKeys.ETCD;

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间(单位毫秒)
     */
    private Long timeout = 5000L;
}
