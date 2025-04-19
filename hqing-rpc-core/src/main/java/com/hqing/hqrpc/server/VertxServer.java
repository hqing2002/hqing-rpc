package com.hqing.hqrpc.server;

/**
 * Vertx服务器接口
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface VertxServer {
    /**
     * 启动服务器
     *
     * @param port 端口号
     */
    void doStart(int port);
}
