package com.hqing.hqrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.serializer.Serializer;
import com.hqing.hqrpc.serializer.SerializerFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        //创建请求体
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        //发送请求
        try {
            //序列化请求体
            byte[] bodyBytes = SERIALIZER.serialize(rpcRequest);
            //发送HTTP请求, 这里地址被硬编码了后面从注册中心拿服务地址
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes).execute()) {
                byte[] resultBytes = httpResponse.bodyBytes();
                //反序列化化结果
                RpcResponse rpcResponse = SERIALIZER.deserialize(resultBytes, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
