package com.hqing.exampleprovider;

import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.registry.LocalRegistry;
import com.hqing.hqrpc.server.HttpServer;
import com.hqing.hqrpc.server.VertxHttpServer;
import com.hqing.hqrpc.utils.ConfigUtils;

/**
 * 服务提供者启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ExampleProviderApplication {
    public static void main(String[] args) {
        //加载自定义配置
        RpcConfig config = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        //使用自定义配置初始化RPC框架
        RpcApplication.init(config);

        //注册到服务注册器
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //启动web服务器, 提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
