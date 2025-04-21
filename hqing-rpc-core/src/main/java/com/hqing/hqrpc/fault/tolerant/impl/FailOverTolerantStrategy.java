package com.hqing.hqrpc.fault.tolerant.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.fault.tolerant.TolerantStrategy;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.server.ServerFactory;
import com.hqing.hqrpc.server.VertxClient;

import java.util.Map;

/**
 * 故障转移(转移到其他服务节点)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //获取上下文中的服务列表
        ServiceMetaInfo serviceMetaInfo = (ServiceMetaInfo) context.getOrDefault("serviceMetaInfo", null);
        //获取上下文中的RpcRequest对象
        RpcRequest rpcRequest = (RpcRequest) context.getOrDefault("rpcRequest", null);
        //参数错误抛出异常
        if (BeanUtil.isEmpty(serviceMetaInfo) || BeanUtil.isEmpty(rpcRequest)) {
            throw new RuntimeException("错误的容错参数", e);
        }
        //获取Vertx客户端
        VertxClient vertxClient = ServerFactory.getVertxClient(RpcApplication.getRpcConfig().getProtocol().getName());

        try {
            //发送请求
            return vertxClient.doRequest(rpcRequest, serviceMetaInfo);
        } catch (Exception ex) {
            //再次异常直接抛出
            throw new RuntimeException("远程服务调用异常", ex);
        }
    }
}
