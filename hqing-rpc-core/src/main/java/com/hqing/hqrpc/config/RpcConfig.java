package com.hqing.hqrpc.config;

import lombok.Data;

/**
 * RPC 框架配置
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "hq-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务消费者配置
     */
    private ConsumerConfig consumer = new ConsumerConfig();

    /**
     * 协议配置
     */
    private ProtocolConfig protocol = new ProtocolConfig();

    /**
     * 注册中心配置
     */
    private RegistryConfig registry = new RegistryConfig();
}
