package com.hqing.hqrpc.config;

import com.hqing.hqrpc.fault.retry.RetryStrategyKeys;
import com.hqing.hqrpc.fault.tolerant.TolerantStrategyKeys;
import com.hqing.hqrpc.loadbalancer.LoadBalancerKeys;
import lombok.Data;

/**
 * 消费者配置类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Data
public class ConsumerConfig {
    /**
     * 开启mock模拟调用
     */
    private boolean mock = false;

    /**
     * 远程服务调用超时时间(单位毫秒)
     */
    private Long timeout = 5000L;

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}
