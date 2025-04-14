package com.hqing.exampleconsumer;

import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.utils.ConfigUtils;

/**
 * 测试读取rpc配置文件
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ConfigLoadTest {
    public static void main(String[] args) {
        RpcConfig config = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        System.out.println(config);
    }
}
