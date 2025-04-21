package com.hqing.hqrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import com.hqing.hqrpc.RpcApplication;
import com.hqing.hqrpc.config.RpcConfig;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.fault.retry.RetryStrategy;
import com.hqing.hqrpc.fault.retry.RetryStrategyFactory;
import com.hqing.hqrpc.fault.tolerant.TolerantStrategy;
import com.hqing.hqrpc.fault.tolerant.TolerantStrategyFactory;
import com.hqing.hqrpc.loadbalancer.LoadBalancer;
import com.hqing.hqrpc.loadbalancer.LoadBalancerFactory;
import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryFactory;
import com.hqing.hqrpc.server.ServerFactory;
import com.hqing.hqrpc.server.VertxClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理(JDK动态代理)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProxy implements InvocationHandler {
    /**
     * 服务版本
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

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
        ServiceMetaInfo serviceMetaInfo = getServiceMetaInfo(serviceName, serviceVersion);
        //获取Rpc框架配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        //获取Vertx客户端
        VertxClient vertxClient = ServerFactory.getVertxClient(rpcConfig.getProtocol().getName());
        try {
            //获取重试策略
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstances(rpcConfig.getConsumer().getRetryStrategy());
            //调用重试策略
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    vertxClient.doRequest(rpcRequest, serviceMetaInfo)
            );
            return rpcResponse.getData();
        } catch (Exception e) {
            //获取容错策略
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstances(rpcConfig.getConsumer().getTolerantStrategy());
            //再次获取服务元信息
            ServiceMetaInfo newServiceMetaInfo = getServiceMetaInfo(serviceName, serviceVersion);
            //构造容错上下文对象
            HashMap<String, Object> context = new HashMap<>();
            context.put("serviceMetaInfo", newServiceMetaInfo);
            context.put("rpcRequest", rpcRequest);
            RpcResponse rpcResponse = tolerantStrategy.doTolerant(context, e);
            return rpcResponse.getData();
        }
    }

    /**
     * 根据服务名从注册中心获取服务元数据
     */
    private ServiceMetaInfo getServiceMetaInfo(String serviceName, String serviceVersion) {
        //获取RPC配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //从工厂中获取注册中心实例
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistry().getName());

        //构造当前请求服务的元信息
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(serviceVersion);

        //服务发现
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
        //获取负载均衡器
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getConsumer().getLoadBalancer());
        //将调用方IP + 请求服务名称作为负载均衡器请求参数
        Map<String, Object> requestParams = new HashMap<>();
        InetAddress inetAddress = NetUtil.getLocalhost();
        requestParams.put("ip", inetAddress.getHostAddress());
        requestParams.put("serviceName", serviceName);
        return loadBalancer.select(requestParams, serviceMetaInfoList);
    }
}
