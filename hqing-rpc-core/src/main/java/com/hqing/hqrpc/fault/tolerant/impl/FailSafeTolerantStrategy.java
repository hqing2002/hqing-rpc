package com.hqing.hqrpc.fault.tolerant.impl;

import com.hqing.hqrpc.fault.tolerant.TolerantStrategy;
import com.hqing.hqrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理异常(只输出异常日志)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("远程调用异常", e);
        return new RpcResponse();
    }
}
