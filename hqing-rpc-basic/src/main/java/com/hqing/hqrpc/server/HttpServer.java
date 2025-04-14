package com.hqing.hqrpc.server;

/**
 * Http服务接口
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface HttpServer {
    /**
     * 启动服务器
     *
     * @param port 端口号
     */
    void doStart(int port);
}
