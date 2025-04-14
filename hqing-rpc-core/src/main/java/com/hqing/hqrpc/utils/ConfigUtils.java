package com.hqing.hqrpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ConfigUtils {

    /**
     * 加载配置文件转换为Bean对象
     * @param clazz Bean对象的class
     * @param prefix 配置文件key前缀
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置文件转换为Bean对象, 支持区分环境
     * @param clazz Bean对象的class
     * @param prefix 配置文件key前缀
     * @param environment 环境名称
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(clazz, prefix);
    }
}
