package com.hqing.hqrpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

/**
 * Tcp请求客户端
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class VertxTcpClient {
    public void sendTcpRequest() {
        //创建Vert.X实例
        Vertx vertx = Vertx.vertx();
        //连接Tcp服务器
        vertx.createNetClient().connect(8080, "localhost", result -> {
            if (result.succeeded()) {
                log.info("连接Tcp服务器成功");
                NetSocket socket = result.result();
                //发送数据
                socket.write("Hello Server, Hello Server, Hello Server!");
                socket.handler(buffer -> {
                    log.info("收到回复{}", buffer.toString());
                });
            } else {
                log.error("Tcp服务器连接失败");
            }
        });
        //关闭TCP连接
        vertx.close();
    }

    public static void main(String[] args) {
        new VertxTcpClient().sendTcpRequest();
    }
}
