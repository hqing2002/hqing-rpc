package com.hqing.exampleprovider;

import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.registry.LocalRegistry;
import com.hqing.hqrpc.server.HttpServer;
import com.hqing.hqrpc.server.VertxHttpServer;

/**
 * 服务提供者启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ExampleProviderApplication {
    public static void main(String[] args) {
        //注册到服务注册器
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //启动web服务器, 提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
