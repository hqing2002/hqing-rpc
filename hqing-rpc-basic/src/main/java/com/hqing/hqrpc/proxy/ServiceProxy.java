package com.hqing.hqrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.serializer.JdkSerializer;
import com.hqing.hqrpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理(JDK动态代理)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        //获取序列化器
        Serializer serializer = new JdkSerializer();

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
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            //发送HTTP请求, 这里地址被硬编码了后面从注册中心拿服务地址
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes).execute()) {
                byte[] resultBytes = httpResponse.bodyBytes();
                //反序列化化结果
                RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
