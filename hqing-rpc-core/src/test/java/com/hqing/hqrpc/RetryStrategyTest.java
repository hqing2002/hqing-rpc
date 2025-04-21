package com.hqing.hqrpc;

import com.hqing.hqrpc.fault.retry.RetryStrategy;
import com.hqing.hqrpc.fault.retry.impl.ExponentialIntervalRetryStrategy;
import org.junit.Test;

/**
 * 测试重试
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class RetryStrategyTest {
    RetryStrategy retryStrategy = new ExponentialIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
        } catch (Exception e) {
            System.out.println("重试多次失败");
            throw new RuntimeException(e);
        }
    }
}
