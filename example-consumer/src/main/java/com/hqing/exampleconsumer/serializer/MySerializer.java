package com.hqing.exampleconsumer.serializer;

import com.hqing.hqrpc.serializer.Serializer;

import java.io.*;

/**
 * 自定义 JDK 序列化器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class MySerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        System.out.println("消费者使用自定义序列化器序列化: " + object);
        if (object == null) {
            throw new IllegalArgumentException("被序列化的对象不能为null");
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(object);
            return outputStream.toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        System.out.println("消费者使用自定义序列化器反序列化");
        if (bytes == null) {
            throw new IllegalArgumentException("字节数组不能为null");
        }
        if (type == null) {
            throw new IllegalArgumentException("目标类型不能为null");
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Object object = objectInputStream.readObject();
            return type.cast(object);
        } catch (ClassNotFoundException e) {
            throw new IOException("找不到对应的类定义: " + e.getMessage(), e);
        } catch (ClassCastException e) {
            throw new IOException("类型不匹配，期望类型: " + type.getName(), e);
        }
    }
}
