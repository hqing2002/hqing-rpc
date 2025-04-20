package com.hqing.hqrpc.protocol;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 协议消息序列化枚举
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Getter
@AllArgsConstructor
public enum ProtocolMessageSerializerEnum {

    JDK(0, "JDK"),
    JSON(1, "JSON"),
    KRYO(2, "KRYO"),
    HESSIAN(3, "HESSIAN"),
    CUSTOMIZE(4, "CUSTOMIZE");

    private final int key;
    private final String value;

    /**
     * 获取序列化器值列表
     */
    public static List<String> getSerializerNameList() {
        return Arrays.stream(values())
                .map(item -> item.value)
                .collect(Collectors.toList());
    }

    /**
     * 根据协议序列化器键获取枚举
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum protocolMessageSerializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (protocolMessageSerializerEnum.key == key) {
                return protocolMessageSerializerEnum;
            }
        }
        return null;
    }

    /**
     * 根据协议序列化器值获取枚举
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        for (ProtocolMessageSerializerEnum protocolMessageSerializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (Objects.equals(protocolMessageSerializerEnum.value, value)) {
                return protocolMessageSerializerEnum;
            }
        }
        return null;
    }
}
