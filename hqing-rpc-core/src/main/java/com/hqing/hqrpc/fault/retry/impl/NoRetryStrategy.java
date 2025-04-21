package com.hqing.hqrpc.fault.retry.impl;

import com.hqing.hqrpc.fault.retry.RetryStrategy;
import com.hqing.hqrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class NoRetryStrategy implements RetryStrategy {

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callBack) throws Exception {
        //直接执行
        return callBack.call();
    }
}
