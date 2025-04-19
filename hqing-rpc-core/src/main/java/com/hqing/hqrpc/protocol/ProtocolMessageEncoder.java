package com.hqing.hqrpc.protocol;


import com.hqing.hqrpc.serializer.Serializer;
import com.hqing.hqrpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 消息编码器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ProtocolMessageEncoder {
    /**
     * 将协议消息编码后放入缓冲区
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        //参数为空直接返回空的buffer
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        //依次向缓存区中写入数据
        ProtocolMessage.Header header = protocolMessage.getHeader();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        //根据序列化器类型获取序列化器
        ProtocolMessageSerializerEnum protocolMessageSerializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (protocolMessageSerializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        //获取序列化器实例
        Serializer serializer = SerializerFactory.getInstance(protocolMessageSerializerEnum.getValue());
        //将body序列化
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        //写入缓存区中消息体的长度和序列化后的消息体
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
