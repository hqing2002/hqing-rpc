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
     * 服务器主机名
     */
    private String severHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 开启mock模拟调用
     */
    private boolean mock = false;
}
