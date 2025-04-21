package com.hqing.hqrpc.fault.tolerant;

/**
 * 容错策略键名常量
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface TolerantStrategyKeys {
    /**
     * 快速失败
     */
    String FAIL_FAST = "FAIL_FAST";

    /**
     * 静默处理
     */
    String FAIL_SAFE = "FAIL_SAFE";

    /**
     * 故障转移
     */
    String FAIL_OVER = "FAIL_OVER";
}
