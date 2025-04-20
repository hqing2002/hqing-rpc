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
    String ROUND_ROBIN = "ROUND_ROBIN";

    /**
     * 随机
     */
    String RANDOM = "RANDOM";

    /**
     * 一致性哈希
     */
    String CONSISTENT_HASH = "CONSISTENT_HASH";
}
