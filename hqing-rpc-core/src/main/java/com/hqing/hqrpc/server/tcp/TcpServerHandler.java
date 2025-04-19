package com.hqing.hqrpc.server.tcp;

import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.protocol.ProtocolMessage;
import com.hqing.hqrpc.protocol.ProtocolMessageDecoder;
import com.hqing.hqrpc.protocol.ProtocolMessageEncoder;
import com.hqing.hqrpc.protocol.ProtocolMessageTypeEnum;
import com.hqing.hqrpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

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
        //处理连接
        netSocket.handler(buffer -> {
            //解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            //处理RPC请求
            RpcResponse rpcResponse = processRpcRequest(protocolMessage.getBody());

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
    }

    /**
     * 处理RPC请求核心逻辑
     */
    private RpcResponse processRpcRequest(RpcRequest rpcRequest) {
        //构造Rpc响应
        RpcResponse rpcResponse = new RpcResponse();

        //请求参数为null, 直接返回
        if (rpcRequest == null) {
            rpcResponse.setMessage("RPC Request Is Null");
            return rpcResponse;
        }
        //读取rpc请求参数
        String serviceName = rpcRequest.getServiceName();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        try {
            //从服务注册器中获取服务实例class, 通过反射获取实例Class
            Class<?> serviceImplClass = LocalRegistry.get(serviceName);
            //调用实现类的无参构造器创建出实例对象
            Object serviceImplObj = serviceImplClass.getDeclaredConstructor().newInstance();
            //根据方法名,方法参数类型获取实例的目标方法
            Method serviceImplMethod = serviceImplClass.getMethod(methodName, parameterTypes);
            //调用目标方法
            Object result = serviceImplMethod.invoke(serviceImplObj, args);
            //封装返回结果
            rpcResponse.setData(result);
            rpcResponse.setDataType(serviceImplMethod.getReturnType());
            rpcResponse.setMessage("ok");
        } catch (Exception e) {
            rpcResponse.setMessage(e.getMessage());
            rpcResponse.setException(e);
        }
        return rpcResponse;
    }
}
