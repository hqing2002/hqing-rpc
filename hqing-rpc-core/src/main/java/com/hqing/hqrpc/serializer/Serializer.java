package com.hqing.hqrpc.serializer;

import java.io.IOException;

/**
 * 序列化接口
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface Serializer {
    /**
     * 将对象序列化为字节数组
     *
     * @param object 需要被序列化的对象
     * @param <T>    被序列化对象的泛型类型
     * @return 包含对象序列化数据的字节数组
     * @throws IOException 当序列化过程中发生I/O错误时抛出
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 将字节数组反序列化为指定类型的对象
     *
     * @param bytes 包含序列化数据的字节数组
     * @param type  目标对象的类型对应的Class对象
     * @param <T>   目标对象的泛型类型
     * @return 反序列化后的目标类型对象
     * @throws IOException 当反序列化过程中发生I/O错误时抛出
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
