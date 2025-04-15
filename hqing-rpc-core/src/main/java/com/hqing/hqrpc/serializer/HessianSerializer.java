package com.hqing.hqrpc.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class HessianSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(bos);
        hessian2Output.writeObject(object);
        hessian2Output.flush();
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(bis);
        return tClass.cast(hessian2Input.readObject(tClass));
    }
}
