package com.hqing.hqrpc.constant;

import java.util.concurrent.TimeUnit;

/**
 * RPC相关常量
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface RpcConstant {
    /**
     * 默认配置文件加载前缀
     */
    String DEFAULT_CONFIG_PREFIX = "rpc";

    /**
     * 默认服务版本
     */
    String DEFAULT_SERVICE_VERSION = "1.0";

    /**
     * 默认超时单位
     */
    TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
}
