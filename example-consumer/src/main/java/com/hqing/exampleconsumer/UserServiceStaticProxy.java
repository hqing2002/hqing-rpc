package com.hqing.exampleconsumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hqing.examplecommon.model.User;
import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.serializer.JdkSerializer;
import com.hqing.hqrpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class UserServiceStaticProxy implements UserService {
    @Override
    public User getUser(String userName) {
        //获取序列化器
        Serializer serializer = new JdkSerializer();

        //创建请求体
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{String.class})
                .args(new Object[]{"hqing"})
                .build();
        //发送请求
        try {
            //序列化请求体
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] resultBytes;
            //发送HTTP Post请求
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes).execute()) {
                resultBytes = httpResponse.bodyBytes();
            }
            //反序列化化结果
            RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
