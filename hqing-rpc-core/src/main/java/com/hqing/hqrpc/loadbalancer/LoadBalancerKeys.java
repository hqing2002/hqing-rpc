package com.hqing.hqrpc.loadbalancer;

/**
 * 负载均衡器键名常量
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface LoadBalancerKeys {
    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";

    /**
     * 随机
     */
    String RANDOM = "random";

    /**
     * 一致性哈希
     */
    String CONSISTENT_HASH = "consistentHash";
}
