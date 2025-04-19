package com.hqing.hqrpc.server.http;

import com.hqing.hqrpc.server.VertxServer;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * Vert.x服务器实现
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class VertxHttpServer implements VertxServer {
    @Override
    public void doStart(int port) {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();

        // 创建HTTP服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        // 启动HTTP服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("服务器启动成功, 监听端口: {}", port);
            } else {
                log.error("服务器启动失败: {}", String.valueOf(result.cause()));
            }
        });
    }
}
