package com.hqing.hqrpc.server.http;

import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.registry.LocalRegistry;
import com.hqing.hqrpc.serializer.Serializer;
import com.hqing.hqrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP请求处理
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {
    //从配置文件中读取序列化器名称,使用工厂获取序列化器
    private static final Serializer SERIALIZER = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getProtocol().getSerializer());

    /**
     * 处理Http请求
     *
     * @param request the event to handle
     */
    @Override
    public void handle(HttpServerRequest request) {
        //记录日志
        log.info("{}收到网络请求, 请求方式: {}, 请求路径: {}",
                Thread.currentThread().getName(), request.method(), request.uri());
        //异步处理HTTP请求
        request.bodyHandler(body -> {
            //反序列化HTTP请求体为Rpc请求对象
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = SERIALIZER.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                log.error("反序列化请求参数异常: ", e);
            }
            //处理RPC请求
            RpcResponse rpcResponse = processRpcRequest(rpcRequest);
            //返回响应信息
            doResponse(request, rpcResponse);
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

    /**
     * 返回请求响应
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // 序列化Rpc响应
            byte[] data = SERIALIZER.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(data));
        } catch (IOException e) {
            httpServerResponse.setStatusCode(500).end("序列化响应失败: " + e.getMessage());
        }
    }
}
