package com.hqing.hqrpc.loadbalancer;

import com.hqing.hqrpc.loadbalancer.impl.RoundRobinLoadBalancer;
import com.hqing.hqrpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 负载均衡器工厂
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class LoadBalancerFactory {
    /**
     * 默认使用轮询
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 获取负载均衡器实例
     */
    public static LoadBalancer getInstance(String key) {
        LoadBalancer loadBalancer;
        try {
            loadBalancer = SpiLoader.getInstance(LoadBalancer.class, key);
        } catch (Exception e) {
            log.error("负载均衡器加载失败, 使用默认的负载均衡器{}", DEFAULT_LOAD_BALANCER.getClass().getName());
            loadBalancer = DEFAULT_LOAD_BALANCER;
        }
        return loadBalancer;
    }
}
