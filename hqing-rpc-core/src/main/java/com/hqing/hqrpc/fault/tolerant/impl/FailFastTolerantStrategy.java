package com.hqing.hqrpc.fault.tolerant.impl;

import com.hqing.hqrpc.fault.tolerant.TolerantStrategy;
import com.hqing.hqrpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败 (立即通知外层调用方)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("远程服务调用服务报错", e);
    }
}
