package com.hqing.hqrpc.server.http;

import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.serializer.Serializer;
import com.hqing.hqrpc.serializer.SerializerFactory;
import com.hqing.hqrpc.server.LocalServiceInvocation;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
            RpcResponse rpcResponse = LocalServiceInvocation.processRpcRequest(rpcRequest);
            //返回响应信息
            doResponse(request, rpcResponse);
        });
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
