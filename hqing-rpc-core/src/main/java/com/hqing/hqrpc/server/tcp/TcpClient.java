package com.hqing.hqrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.protocol.*;
import com.hqing.hqrpc.server.VertxClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Tcp请求客户端
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class TcpClient implements VertxClient {
    private static final Long TIMEOUT = RpcApplication.getRpcConfig().getProtocol().getTimeout();

    @Override
    public RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Exception {
        //获取序列化器配置
        String serializerKey = RpcApplication.getRpcConfig().getProtocol().getSerializer();
        //创建Vertx Tcp连接客户端
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        //接收Tcp异步响应
        CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();
        //连接Tcp服务器
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
            if (result.succeeded()) {
                log.info("连接Tcp服务器成功");
                NetSocket netSocket = result.result();
                //构造请求消息
                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                //设置魔数和版本号
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                //设置序列化器类型
                ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByValue(serializerKey);
                if (serializerEnum == null) {
                    throw new RuntimeException("使用无效的序列化器key");
                }
                header.setSerializer((byte) serializerEnum.getKey());
                //设置Tcp协议类型为请求类型
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                //设置请求Id
                header.setRequestId(IdUtil.getSnowflakeNextId());
                //添加请求头和请求体
                protocolMessage.setHeader(header);
                protocolMessage.setBody(rpcRequest);

                try {
                    //使用编码器对消息对象编码
                    Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                    //发送到Tcp服务器
                    netSocket.write(encodeBuffer);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息编码错误");
                }

                //设置消息接收
                TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                    try {
                        //使用解码器解码
                        ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);

                        //完成了响应
                        responseCompletableFuture.complete(rpcResponseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException("解码器解码响应失败");
                    }
                });

                //设置Tcp响应处理器
                netSocket.handler(tcpBufferHandlerWrapper);
            } else {
                throw new RuntimeException("Tcp服务器连接失败");
            }
        });
        //阻塞, 直到响应完成(调用complete方法), 才会继续向下执行
        RpcResponse rpcResponse = responseCompletableFuture.orTimeout(TIMEOUT, RpcConstant.DEFAULT_TIME_UNIT).get();
        //关闭Tcp连接
        netClient.close();
        //返回响应结果
        return rpcResponse;
    }
}
