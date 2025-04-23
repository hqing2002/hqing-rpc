package com.hqing.hqrpc.server;

import com.hqing.hqrpc.model.RpcRequest;
import com.hqing.hqrpc.model.RpcResponse;
import com.hqing.hqrpc.model.ServiceLocalRegisterInfo;
import com.hqing.hqrpc.registry.LocalRegistry;

import java.lang.reflect.Method;

/**
 * 处理本地服务调用
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class LocalServiceInvocation {
    /**
     * 处理RPC请求核心逻辑
     */
    public static RpcResponse processRpcRequest(RpcRequest rpcRequest) {
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
        String serviceVersion = rpcRequest.getServiceVersion();

        try {
            //从服务注册器中获取服务本地注册信息(实现类的class, 实例对象)
            ServiceLocalRegisterInfo<?> serviceLocalRegisterInfo = LocalRegistry.get(serviceName + serviceVersion);
            Class<?> serviceImplClass = serviceLocalRegisterInfo.getServiceImplClass();
            Object instance = serviceLocalRegisterInfo.getInstance();
            //根据方法名,方法参数类型获取实例的目标方法
            Method serviceImplMethod = serviceImplClass.getMethod(methodName, parameterTypes);
            //调用目标方法
            Object result = serviceImplMethod.invoke(instance, args);
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
}
