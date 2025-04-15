package com.hqing.hqrpc.serializer;

import com.hqing.hqrpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 序列化器工厂
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class SerializerFactory {
    /**
     * 默认JDK序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 获取序列化器实例
     *
     * @param key 序列化器key
     */
    public static Serializer getInstance(String key) {
        Serializer serializer;
        log.info("加载序列化器: {}", key);
        try {
            serializer = SpiLoader.getInstance(Serializer.class, key);
        } catch (Exception e) {
            log.error("序列化器加载失败, 使用默认序列化器: {}", DEFAULT_SERIALIZER.getClass().getName());
            serializer = DEFAULT_SERIALIZER;
        }
        return serializer;
    }
}
