package com.hqing.hqrpc;

import cn.hutool.core.net.NetUtil;
import com.hqing.hqrpc.loadbalancer.LoadBalancer;
import com.hqing.hqrpc.loadbalancer.impl.ConsistentHashLoadBalancer;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试负载均衡
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class LoadBalancerTest {
    final LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    @Test
    public void select() {
        //构造参数
        Map<String, Object> requestParams = new HashMap<>();
        InetAddress inetAddress = NetUtil.getLocalhost();
        requestParams.put("ip", inetAddress.getHostAddress());
        requestParams.put("serviceName", "userService");
        // 服务列表
        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
        serviceMetaInfo1.setServiceName("myService");
        serviceMetaInfo1.setServiceHost("localhost");
        serviceMetaInfo1.setServicePort(8080);

        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
        serviceMetaInfo2.setServiceName("myService");
        serviceMetaInfo2.setServiceHost("localhost");
        serviceMetaInfo2.setServicePort(9090);

        ServiceMetaInfo serviceMetaInfo3 = new ServiceMetaInfo();
        serviceMetaInfo3.setServiceName("myService");
        serviceMetaInfo3.setServiceHost("localhost");
        serviceMetaInfo3.setServicePort(10000);
        List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(serviceMetaInfo1, serviceMetaInfo2, serviceMetaInfo3);
        // 连续调用
        for (int i = 0; i < 5; i++) {
            ServiceMetaInfo serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            System.out.println(serviceMetaInfo);
        }
        //构造参数
        requestParams.put("serviceName", "orderService");
        // 连续调用
        for (int i = 0; i < 5; i++) {
            ServiceMetaInfo serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            System.out.println(serviceMetaInfo);
        }
    }
}
