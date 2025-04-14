package com.hqing.hqrpc.serializer;

import java.io.*;

/**
 * JDK 序列化器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class JdkSerializer implements Serializer {
    /**
     * 使用Java原生序列化将对象转换为字节数组
     *
     * @param object 需要被序列化的对象（必须实现Serializable接口）
     * @return 包含序列化数据的字节数组
     * @throws IOException              当发生以下情况时抛出：
     *                                  - 对象未实现Serializable接口
     *                                  - I/O操作失败
     * @throws IllegalArgumentException 如果输入对象为null
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        if (object == null) {
            throw new IllegalArgumentException("被序列化的对象不能为null");
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(object);
            return outputStream.toByteArray();
        }
    }

    /**
     * 使用Java原生反序列化将字节数组转换为对象
     *
     * @param bytes 包含序列化数据的字节数组（不能为null）
     * @param type  目标对象的类型对应的Class对象（例如：MyClass.class）
     * @return 反序列化生成的目标类型对象
     * @throws IOException              当发生以下情况时抛出：
     *                                  - 字节数组格式无效
     *                                  - 类版本不匹配
     *                                  - I/O操作失败
     * @throws IllegalArgumentException 如果输入参数为null
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
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
