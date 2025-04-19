package com.hqing.hqrpc.protocol;

/**
 * 协议常量
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface ProtocolConstant {
    /**
     * 消息头长度(17个字节)
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
