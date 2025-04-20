package com.hqing.hqrpc.server.tcp;

import com.hqing.hqrpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;


/**
 * 装饰者模式(使用RecordParser对原有的buffer处理能力进行增强, 解决粘包问题)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        //将原本的Handler<Buffer>处理为RecordParser对象
        this.recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        //构造RecordParser
        RecordParser recordParser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        recordParser.setOutput(new Handler<Buffer>() {
            //设置初始化大小
            int size = -1;
            //将读取的结果进行拼装
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                //读取请求头
                if (-1 == size) {
                    //从第13个字节开始读就是请求体长度
                    size = buffer.getInt(13);
                    //设置下一次读取的长度
                    recordParser.fixedSizeMode(size);
                    //这次读取的请求头放进结果中
                    resultBuffer.appendBuffer(buffer);
                } else {
                    //读取请求体放入结果buffer中
                    resultBuffer.appendBuffer(buffer);
                    //参加已经拼接为完整的buffer, 调用原本的bufferHandler处理buffer
                    bufferHandler.handle(resultBuffer);
                    //重置下一轮
                    recordParser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return recordParser;
    }
}
