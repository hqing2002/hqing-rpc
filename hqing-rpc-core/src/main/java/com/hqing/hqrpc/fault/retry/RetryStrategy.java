package com.hqing.hqrpc.fault.retry;

import com.hqing.hqrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface RetryStrategy {
    /**
     * 重试
     *
     * @param callBack 需要重试的代码快
     */
    RpcResponse doRetry(Callable<RpcResponse> callBack) throws Exception;
}
