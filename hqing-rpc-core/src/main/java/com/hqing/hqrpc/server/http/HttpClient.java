package com.hqing.hqrpc.server.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.serializer.Serializer;
import com.hqing.hqrpc.serializer.SerializerFactory;
import com.hqing.hqrpc.server.VertxClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Http客户端
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class HttpClient implements VertxClient {
    private static final Long TIMEOUT = RpcApplication.getRpcConfig().getConsumer().getTimeout();

    @Override
    public RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Exception {
        //输出日志
        log.info("发起远程调用, 请求信息: {}", serviceMetaInfo);
        //获取序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getProtocol().getSerializer());
        //序列化请求体
        byte[] bodyBytes = serializer.serialize(rpcRequest);
        //发送Http请求
        HttpResponse httpResponse = HttpRequest
                .post(serviceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .timeout(TIMEOUT.intValue())
                .execute();
        //获取Http响应
        byte[] resultBytes = httpResponse.bodyBytes();
        //关闭http连接
        httpResponse.close();
        //反序列化结果并返回
        return serializer.deserialize(resultBytes, RpcResponse.class);
    }
}
