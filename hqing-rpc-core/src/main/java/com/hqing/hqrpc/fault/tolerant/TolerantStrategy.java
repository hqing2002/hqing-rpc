package com.hqing.hqrpc.fault.tolerant;

import com.hqing.hqrpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface TolerantStrategy {
    /**
     * 容错
     * @param context 容错处理上下文
     * @param e 异常
     * @return 容错处理结果
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
