package com.hqing.hqrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 协议消息类型枚举
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Getter
@AllArgsConstructor
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;

    /**
     * 根据协议类型value获取枚举
     */
    public static ProtocolMessageTypeEnum getEnumByValue(int value) {
        for (ProtocolMessageTypeEnum protocolMessageStatusEnum : ProtocolMessageTypeEnum.values()) {
            if (protocolMessageStatusEnum.key == value) {
                return protocolMessageStatusEnum;
            }
        }
        return null;
    }
}
