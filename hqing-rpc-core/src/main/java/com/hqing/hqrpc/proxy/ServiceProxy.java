package com.hqing.hqrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryFactory;
import com.hqing.hqrpc.server.ServerFactory;
import com.hqing.hqrpc.server.VertxClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理(JDK动态代理)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

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
        //获取服务元信息
        ServiceMetaInfo serviceMetaInfo = getServiceMetaInfo(serviceName);

        //获取Vertx客户端
        VertxClient vertxClient = ServerFactory.getVertxClient(RpcApplication.getRpcConfig().getProtocol().getName());
        try {
            return vertxClient.doRequest(rpcRequest, serviceMetaInfo).getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据服务名从注册中心获取服务元数据
     */
    private ServiceMetaInfo getServiceMetaInfo(String serviceName) {
        //获取RPC配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //从工厂中获取注册中心实例
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistry().getName());

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
