package com.hqing.hqrpc.server.tcp;

import com.hqing.hqrpc.server.VertxServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * Vert.x TCP 服务器实现
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class VertxTcpServer implements VertxServer {
    @Override
    public void doStart(int port) {
        //创建Vert.x实例
        Vertx vertx = Vertx.vertx();

        //创建TCP服务器
        NetServer server = vertx.createNetServer();

        //处理请求
        server.connectHandler(new TcpServerHandler());

        //启动Tcp服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("Tcp服务器启动成功, 监听端口: {}", port);
            } else {
                log.error("Tcp服务器启动失败: {}", String.valueOf(result.cause()));
            }
        });
    }
}
