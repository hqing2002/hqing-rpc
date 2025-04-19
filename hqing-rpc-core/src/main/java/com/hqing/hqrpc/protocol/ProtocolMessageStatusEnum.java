package com.hqing.hqrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 协议消息状态枚举
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Getter
@AllArgsConstructor
public enum ProtocolMessageStatusEnum {

    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private final String text;
    private final int value;

    /**
     * 根据协议状态value获取枚举
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum protocolMessageStatusEnum : ProtocolMessageStatusEnum.values()) {
            if (protocolMessageStatusEnum.value == value) {
                return protocolMessageStatusEnum;
            }
        }
        return null;
    }
}
