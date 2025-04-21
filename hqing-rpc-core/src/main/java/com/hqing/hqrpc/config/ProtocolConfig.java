package com.hqing.hqrpc.config;

import com.hqing.hqrpc.serializer.SerializerKeys;
import com.hqing.hqrpc.server.ServerKeys;
import lombok.Data;

/**
 * 协议配置类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Data
public class ProtocolConfig {
    /**
     * 协议名称
     */
    private String name = ServerKeys.TCP;

    /**
     * 服务暴露端口
     */
    private Integer port = 20880;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;
}
