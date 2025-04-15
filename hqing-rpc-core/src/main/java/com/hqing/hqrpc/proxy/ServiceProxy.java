package com.hqing.hqrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryFactory;
import com.hqing.hqrpc.serializer.Serializer;
import com.hqing.hqrpc.serializer.SerializerFactory;
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
            //获取请求地址
            String serviceAddress = getServiceAddress(serviceName);

            //发送HTTP请求, 这里地址被硬编码了后面从注册中心拿服务地址
            try (HttpResponse httpResponse = HttpRequest.post(serviceAddress)
                    .body(bodyBytes).execute()) {
                byte[] resultBytes = httpResponse.bodyBytes();
                //反序列化化结果
                RpcResponse rpcResponse = SERIALIZER.deserialize(resultBytes, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            log.error("RPC 调用失败", e);
        }
        return null;
    }

    private String getServiceAddress(String serviceName) {
        //获取RPC配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //从工厂中获取RPC实例
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
        return selectedServiceMetaInfo.getServiceAddress();
    }
}
