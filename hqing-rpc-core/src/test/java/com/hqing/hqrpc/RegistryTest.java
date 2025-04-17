package com.hqing.hqrpc;

import cn.hutool.json.JSONUtil;
import com.hqing.hqrpc.config.RegistryConfig;
import com.hqing.hqrpc.model.ServiceMetaInfo;
import com.hqing.hqrpc.registry.impl.EtcdRegistry;
import com.hqing.hqrpc.registry.Registry;
import com.hqing.hqrpc.registry.RegistryServiceCache;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


/**
 * 测试注册中心
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class RegistryTest {
    final Registry registry = new EtcdRegistry();

    public static void main(String[] args) {
        RegistryServiceCache registryServiceCache = new RegistryServiceCache();
        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
        serviceMetaInfo1.setServiceName("userService");
        serviceMetaInfo1.setServiceHost("localhost");
        serviceMetaInfo1.setServicePort(8080);

        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
        serviceMetaInfo2.setServiceName("userService");
        serviceMetaInfo2.setServiceHost("localhost");
        serviceMetaInfo2.setServicePort(8081);

        registryServiceCache.writeCache(serviceMetaInfo1.getServiceKey(), serviceMetaInfo1.getServiceNodeKey(), serviceMetaInfo1);
        registryServiceCache.writeCache(serviceMetaInfo2.getServiceKey(), serviceMetaInfo2.getServiceNodeKey(), serviceMetaInfo2);
        List<ServiceMetaInfo> serviceMetaInfos = registryServiceCache.readCache(serviceMetaInfo1.getServiceKey());
        System.out.println(serviceMetaInfos);

        ServiceMetaInfo serviceMetaInfo3 = new ServiceMetaInfo();
        serviceMetaInfo3.setServiceName("userService");
        serviceMetaInfo3.setServiceHost("localhost");
        serviceMetaInfo3.setServicePort(8080);
        serviceMetaInfo3.setServiceGroup("123");
        registryServiceCache.writeCache(serviceMetaInfo3.getServiceKey(), serviceMetaInfo3.getServiceNodeKey(), serviceMetaInfo3);
        List<ServiceMetaInfo> serviceMetaInfos2 = registryServiceCache.readCache(serviceMetaInfo1.getServiceKey());
        System.out.println(serviceMetaInfos2);
    }

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("userService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(8080);
        registry.register(serviceMetaInfo);

        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("userService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(8081);
        registry.register(serviceMetaInfo);

        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("userService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(8082);
        registry.register(serviceMetaInfo);

        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("itemService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(8083);

        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("itemService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(8084);
        registry.register(serviceMetaInfo);
    }

    @Test
    public void unRegister() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("hqingService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(8088);
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("hqingService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        for (ServiceMetaInfo metaInfo : serviceMetaInfoList) {
            System.out.println(JSONUtil.toJsonStr(metaInfo));
        }
    }

    /**
     * 测试心跳检测
     */
    @Test
    public void heartBeat() throws Exception {
        register();
        //阻塞1分钟, 一分钟后程序退出代表节点失效
        Thread.sleep(60 * 1000L);
    }
}
