package com.hqing.hqrpc.fault.retry;

/**
 * 重试策略键名
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface RetryStrategyKeys {
    /**
     * 不重试
     */
    String NO = "NO";

    /**
     * 固定时间间隔
     */
    String FIXED_INTERVAL = "FIXED_INTERVAL";

    /**
     * 指数时间间隔
     */
    String EXPONENTIAL_INTERVAL = "EXPONENTIAL_INTERVAL";

    /**
     * 递增时间间隔
     */
    String INCREMENTING_INTERVAL = "INCREMENTING_INTERVAL";

    /**
     * 随机时间间隔
     */
    String RANDOM_INTERVAL = "RANDOM_INTERVAL";
}
