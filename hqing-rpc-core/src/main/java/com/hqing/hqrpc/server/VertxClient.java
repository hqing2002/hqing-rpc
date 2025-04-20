package com.hqing.hqrpc.server;

import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.model.ServiceMetaInfo;

/**
 * Vertx客户端
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface VertxClient {
    /**
     * 发送网络请求并解析
     *
     * @param rpcRequest      rpc请求对象
     * @param serviceMetaInfo 请求服务的元信息
     * @return rpc相应对象
     */
    RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Exception;
}
