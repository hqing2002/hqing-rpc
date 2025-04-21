package com.hqing.hqrpc.fault.retry.impl;

import com.github.rholder.retry.*;
import com.hqing.hqrpc.constant.RpcConstant;
import com.hqing.hqrpc.fault.retry.RetryStrategy;
import com.hqing.hqrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 递增时间间隔重试
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class IncrementingIntervalRetryStrategy implements RetryStrategy {
    /**
     * 初始等待时间
     */
    private static final Long INITIAL_SLEEP_TIME = 3000L;

    /**
     * 递增时间
     */
    private static final Long INCREMENT_TIME = 5000L;

    /**
     * 总运行次数(首次执行+重试次数)
     */
    private static final int RETRY_COUNT = 3;

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callBack) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                //抛出Exception异常时重试
                .retryIfExceptionOfType(Exception.class)
                //递增时间间隔策略
                .withWaitStrategy(WaitStrategies.incrementingWait(
                        INITIAL_SLEEP_TIME,
                        RpcConstant.DEFAULT_TIME_UNIT,
                        INCREMENT_TIME,
                        RpcConstant.DEFAULT_TIME_UNIT))
                //超过最大重试次数停止
                .withStopStrategy(StopStrategies.stopAfterAttempt(RETRY_COUNT))
                //监听重试, 打印重试次数
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("执行间隔: {}", attempt.getDelaySinceFirstAttempt());
                        log.info("运行次数: {}", attempt.getAttemptNumber());
                    }
                }).build();

        return retryer.call(callBack);
    }
}
