package com.hqing.hqrpc.fault.retry;

import com.hqing.hqrpc.fault.retry.impl.NoRetryStrategy;
import com.hqing.hqrpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 重试策略工厂
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class RetryStrategyFactory {
    /**
     * 默认不重试
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 获取重试策略实例
     */
    public static RetryStrategy getInstances(String key) {
        RetryStrategy retryStrategy;
        try {
            retryStrategy = SpiLoader.getInstance(RetryStrategy.class, key);
        } catch (Exception e) {
            log.error("加载重试策略: {}失败, 使用默认的重试策略: {}", key, DEFAULT_RETRY_STRATEGY.getClass().getName());
            retryStrategy = DEFAULT_RETRY_STRATEGY;
        }
        return retryStrategy;
    }
}
