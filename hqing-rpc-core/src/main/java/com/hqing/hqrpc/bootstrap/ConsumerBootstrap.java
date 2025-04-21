package com.hqing.hqrpc.bootstrap;

import com.hqing.hqrpc.RpcApplication;

/**
 * 服务消费者初始化
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ConsumerBootstrap {

    /**
     * 初始化
     */
    public static void init() {
        //Rpc框架初始化
        RpcApplication.getRpcConfig();
    }
}
