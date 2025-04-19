package com.hqing.hqrpc;

import cn.hutool.core.util.IdUtil;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.protocol.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SocketAddress;
import org.junit.Test;

import java.io.IOException;

/**
 * 自定义协议测试
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ProtocolMessageTest {
    public static void main(String[] args) throws IOException {
        //构造消息头
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic((ProtocolConstant.PROTOCOL_MAGIC));
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());

        //构造消息头体
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("userService");
        rpcRequest.setMethodName("getUser");
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"hqing"});

        //构造消息
        ProtocolMessage<RpcRequest> rpcRequestProtocolMessage = new ProtocolMessage<>();
        rpcRequestProtocolMessage.setHeader(header);
        rpcRequestProtocolMessage.setBody(rpcRequest);

        //编码器编码
        Buffer encode = ProtocolMessageEncoder.encode(rpcRequestProtocolMessage);
        ProtocolMessage<?> decode = ProtocolMessageDecoder.decode(encode);
        System.out.println(decode);
    }

    @Test
    public void test() {

        SocketAddress socketAddress = SocketAddress.domainSocketAddress("localhost:8080");
        System.out.println(socketAddress);
    }
}
