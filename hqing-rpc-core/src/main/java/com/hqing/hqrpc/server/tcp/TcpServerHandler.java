package com.hqing.hqrpc.server.tcp;

import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.protocol.ProtocolMessage;
import com.hqing.hqrpc.protocol.ProtocolMessageDecoder;
import com.hqing.hqrpc.protocol.ProtocolMessageEncoder;
import com.hqing.hqrpc.protocol.ProtocolMessageTypeEnum;
import com.hqing.hqrpc.server.LocalServiceInvocation;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * TCP请求处理
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {

    /**
     * 接收Tcp请求
     *
     * @param netSocket the event to handle
     */
    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            //解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            //处理RPC请求
            RpcResponse rpcResponse = LocalServiceInvocation.processRpcRequest(protocolMessage.getBody());

            //返回请求响应
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);

            try {
                Buffer encode = ProtocolMessageEncoder.encode(rpcResponseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
        netSocket.handler(tcpBufferHandlerWrapper);
    }
}
