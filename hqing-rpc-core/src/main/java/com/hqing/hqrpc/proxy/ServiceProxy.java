package com.hqing.hqrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.protocol.*;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryFactory;
import com.hqing.hqrpc.serializer.Serializer;
import com.hqing.hqrpc.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理(JDK动态代理)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {
    private static final Serializer SERIALIZER = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    /**
     * 调用代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String serviceName = method.getDeclaringClass().getName();
        //创建请求体
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        //发送请求
        try {
            //序列化请求体
            byte[] bodyBytes = SERIALIZER.serialize(rpcRequest);

            //获取服务元信息
            ServiceMetaInfo serviceMetaInfo = getServiceMetaInfo(serviceName);

            //发送HTTP请求
//            try (HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo.getServiceAddress())
//                    .body(bodyBytes).execute()) {
//                byte[] resultBytes = httpResponse.bodyBytes();
//                //反序列化化结果
//                RpcResponse rpcResponse = SERIALIZER.deserialize(resultBytes, RpcResponse.class);
//                return rpcResponse.getData();
//            }
            //发送Tcp请求
            Vertx vertx = Vertx.vertx();
            //创建Vert.X Tcp连接客户端
            NetClient netClient = vertx.createNetClient();
            CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();
            //连接tcp服务器
            netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
                if (result.succeeded()) {
                    log.info("连接Tcp服务器成功");
                    NetSocket netSocket = result.result();

                    //构造请求消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumBySerializerValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    //消息编码
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        netSocket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }
                    //接收响应
                    netSocket.handler(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            responseCompletableFuture.complete(rpcResponseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    throw new RuntimeException("Tcp服务器连接失败");
                }
            });
            //阻塞, 直到响应完成, 才会继续向下执行
            RpcResponse rpcResponse = responseCompletableFuture.get();
            //关闭连接
            netClient.close();
            return rpcResponse.getData();
        } catch (Exception e) {
            log.error("RPC 调用失败", e);
        }
        return null;
    }

    /**
     * 根据服务名从注册中心获取服务元数据
     */
    private ServiceMetaInfo getServiceMetaInfo(String serviceName) {
        //获取RPC配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //从工厂中获取注册中心实例
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

        //构造当前请求服务的元信息
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

        //服务发现
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
        //todo 负载均衡
        ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
        return selectedServiceMetaInfo;
    }
}
