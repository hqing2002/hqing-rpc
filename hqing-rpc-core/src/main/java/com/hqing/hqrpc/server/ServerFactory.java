package com.hqing.hqrpc.server;

import com.hqing.hqrpc.server.tcp.TcpClient;
import com.hqing.hqrpc.server.tcp.VertxTcpServer;
import com.hqing.hqrpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 网络服务工厂
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class ServerFactory {
    /**
     * 默认采用Tcp服务器
     */
    private static final VertxServer DEFAULT_VERTX_SERVER = new VertxTcpServer();

    /**
     * 默认采用Tcp协议
     */
    private static final VertxClient DEFAULT_VERTX_CLIENT = new TcpClient();

    static {
        SpiLoader.load(VertxServer.class);
        SpiLoader.load(VertxClient.class);
    }

    /**
     * 获取网络服务器实例
     *
     * @param key 服务器key
     */
    public static VertxServer getVertxServer(String key) {
        VertxServer vertxServer;
        log.info("加载服务器: {}", key);
        try {
            vertxServer = SpiLoader.getInstance(VertxServer.class, key);
        } catch (Exception e) {
            log.error("加载服务器失败, 使用默认的服务器: {}", DEFAULT_VERTX_SERVER.getClass().getName());
            vertxServer = DEFAULT_VERTX_SERVER;
        }
        return vertxServer;
    }

    /**
     * 获取客户端实例
     *
     * @param key 服务器key
     */
    public static VertxClient getVertxClient(String key) {
        VertxClient vertxClient;
        try {
            vertxClient = SpiLoader.getInstance(VertxClient.class, key);
        } catch (Exception e) {
            log.error("加载协议失败, 使用默认协议: {}", DEFAULT_VERTX_CLIENT.getClass().getName());
            vertxClient = DEFAULT_VERTX_CLIENT;
        }
        return vertxClient;
    }
}
