package com.hqing.hqrpc.fault.tolerant;

import com.hqing.hqrpc.fault.tolerant.impl.FailFastTolerantStrategy;
import com.hqing.hqrpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 容错策略工厂
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class TolerantStrategyFactory {
    /**
     * 默认快速失败
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 获取容错策略实例
     */
    public static TolerantStrategy getInstances(String key) {
        TolerantStrategy tolerantStrategy;
        try {
            tolerantStrategy = SpiLoader.getInstance(TolerantStrategy.class, key);
        } catch (Exception e) {
            log.error("加载容错策略: {}失败, 使用默认的容错策略: {}", key, DEFAULT_TOLERANT_STRATEGY.getClass().getName());
            tolerantStrategy = DEFAULT_TOLERANT_STRATEGY;
        }
        return tolerantStrategy;
    }
}
