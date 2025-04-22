package com.hqing.hqingrpcspringbootstarter.utils;

import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 注解扫描工具类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class AnnotationScannerUtil {
    /**
     * 扫描指定包下的类，找到带有特定类注解或字段注解的类
     *
     * @param basePackages    包
     * @param classAnnotation 类注解
     * @param fieldAnnotation 字段注解
     */
    public static Map<Class<?>, AnnotatedInfo> scanPackageWithAnnotation(
            String[] basePackages,
            Class<? extends Annotation> classAnnotation,
            Class<? extends Annotation> fieldAnnotation) {
        Map<Class<?>, AnnotatedInfo> resultMap = new HashMap<>();

        //获取Spring的资源扫描器
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        //扫描包
        for (String basePackage : basePackages) {
            //将包名转换为路径格式(例如 com.example -> classpath*:com/example/**/*.class)指定文件类型为class文件
            String packagePath = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";
            Resource[] resources;
            //获取包下的所有class文件
            try {
                resources = resourcePatternResolver.getResources(packagePath);
            } catch (IOException e) {
                throw new RuntimeException("资源读取失败, 包名参数错误", e);
            }
            //遍历class文件
            for (Resource resource : resources) {
                try {
                    //获取该class文件的元信息对象
                    ClassMetadata classMetadata = new SimpleMetadataReaderFactory()
                            .getMetadataReader(resource).getClassMetadata();

                    //获取该类的class对象
                    Class<?> beanClass = Class.forName(classMetadata.getClassName());

                    //检查类注解
                    boolean hasClassAnnotation = beanClass.isAnnotationPresent(classAnnotation);

                    //检查字段注解
                    List<Field> fieldsWithAnnotation = Arrays.stream(beanClass.getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(fieldAnnotation))
                            .collect(Collectors.toList());

                    //如果有类注解或者有字段注解就存入结果中
                    if (hasClassAnnotation || !fieldsWithAnnotation.isEmpty()) {
                        AnnotatedInfo annotatedInfo = new AnnotatedInfo(hasClassAnnotation, fieldsWithAnnotation);
                        resultMap.put(beanClass, annotatedInfo);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("包扫描失败", e);
                }
            }
        }
        return resultMap;
    }

    /**
     * 结果信息类, 用于存储该类是否包含类注解, 或者哪些字段包含了字段注解
     */
    @Data
    public static class AnnotatedInfo {
        private final boolean hasClassAnnotation;
        private final List<Field> fieldsWithAnnotation;
    }
}
