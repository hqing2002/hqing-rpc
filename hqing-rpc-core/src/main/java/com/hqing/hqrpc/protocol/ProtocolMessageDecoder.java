package com.hqing.hqrpc.protocol;

import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.serializer.Serializer;
import com.hqing.hqrpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 消息解码器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ProtocolMessageDecoder {

    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();

        //按照存入的顺序读取
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("非法请求, magic不匹配");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        //解决粘包问题, 只读取指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //根据消息头中的序列化器key获取序列化实例
        ProtocolMessageSerializerEnum protocolMessageSerializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (protocolMessageSerializerEnum == null) {
            throw new RuntimeException("消息序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(protocolMessageSerializerEnum.getValue());

        //根据消息头的请求类型进行反序列化
        ProtocolMessageTypeEnum protocolMessageTypeEnum = ProtocolMessageTypeEnum.getEnumByValue(header.getType());
        if (protocolMessageTypeEnum == null) {
            throw new RuntimeException("消息类型不存在");
        }
        switch (protocolMessageTypeEnum) {
            case REQUEST:
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, rpcRequest);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, rpcResponse);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }
}
