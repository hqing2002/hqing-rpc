package com.hqing.hqrpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 配置工具类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class ConfigUtils {

    /**
     * 加载配置文件转换为Bean对象
     *
     * @param clazz  Bean对象的class
     * @param prefix 配置文件key前缀
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置文件转换为Bean对象, 支持区分环境, 支持properties/yml/yaml读取
     *
     * @param clazz       Bean对象的class
     * @param prefix      配置文件key前缀
     * @param environment 环境名称
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment) {
        //文件名前缀(application-env)
        StringBuilder baseName = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            baseName.append("-").append(environment);
        }
        //定义文件名后缀数组, 按照元素顺序设置读取优先级
        final String[] extensions = {".properties", ".yml", ".yaml"};

        //获取类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (String extension : extensions) {
            //拼接配置文件名称
            String configPath = baseName + extension;
            //使用类加载器获取文件
            URL fileUrl = classLoader.getResource(configPath);
            //文件不存在
            if (fileUrl == null) {
                continue;
            }
            //文件存在, 如果类型是properties
            if (".properties".equals(extension)) {
                Props props = new Props(configPath, StandardCharsets.UTF_8);
                //将得到的属性转换为实例对象返回, 如果属性为空则返回空参构造器创建的默认实例对象
                return props.toBean(clazz, prefix);
            } else {
                //加载yml/yaml配置文件
                try {
                    Dict dict = YamlUtil.loadByPath(configPath);
                    Object bean = dict.getBean(prefix);
                    //如果读取不到属性就给个空对象, 下面就会直接给一个默认实例对象
                    if (bean == null) {
                        bean = new Object();
                    }
                    return BeanUtil.copyProperties(bean, clazz);
                } catch (Exception e) {
                    //如果配置文件读取异常直接返回默认实例对象
                    return BeanUtil.copyProperties(new Object(), clazz);
                }
            }
        }
        //如果都没有就返回默认实例对象
        return BeanUtil.copyProperties(new Object(), clazz);
    }
}
