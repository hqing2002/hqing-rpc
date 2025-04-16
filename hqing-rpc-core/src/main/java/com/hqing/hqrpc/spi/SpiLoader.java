package com.hqing.hqrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.hqing.hqrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spi加载器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类: 接口名 -> (key -> 实现类class)
     */
    private static final Map<String, Map<String, Class<?>>> LOADER_MAP = new ConcurrentHashMap<>();

    /**
     * 储存实例缓存(避免重复new): 类路径 -> 对象实例 (单例模式)
     */
    private static final Map<String, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_USER_SPI_DIR = "META-INF/rpc/service/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_USER_SPI_DIR};

    /**
     * 需要加载的接口列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = List.of(Serializer.class);

    /**
     * 加载所有SPI接口
     */
    public static void loadAll() {
        log.info("加载所有SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 获取某个接口的某个实例
     *
     * @param tClass 接口类
     * @param key    实现类的key
     */
    public static <T> T getInstance(Class<T> tClass, String key) {
        //接口名称
        String tClassName = tClass.getName();

        //获取该接口的ClassMap(key -> 实现类class)
        Map<String, Class<?>> keyClassMap = LOADER_MAP.get(tClassName);

        //查找的接口未加载
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载%s类型", tClassName));
        }
        //查找的接口的key不存在
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader %s类型不存在key=%s的实现", tClassName, key));
        }
        //获取到需要加载的实现类class
        Class<?> implClass = keyClassMap.get(key);
        //从实例对象缓存中尝试获取
        String implClassName = implClass.getName();
        //实例不存在缓存中就存入一下
        if (!INSTANCE_CACHE.containsKey(implClassName)) {
            try {
                INSTANCE_CACHE.put(implClassName, implClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                String errorMessage = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMessage, e);
            }
        }
        return tClass.cast(INSTANCE_CACHE.get(implClassName));
    }

    /**
     * 加载某个类型的接口
     *
     * @param loadClass 类型
     */
    public static void load(Class<?> loadClass) {
        log.info("加载SPI, 类型为:{}", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();

        //扫描路径文件,先扫描系统目录再扫描用户目录,用户定义同名key可以覆盖系统的
        for (String scanDir : SCAN_DIRS) {
            //获取目录下的所有资源
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            //读取每一个资源文件
            for (URL resource : resources) {
                try {
                    //获取文件输入流
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    //从文件输入流获取缓存
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //读取每一行
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        //文件内容,key=全类名分割
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            //通过全类名获取Class对象
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("SPI Resource Load Error", e);
                }
            }
        }
        //储存得到的内容
        LOADER_MAP.put(loadClass.getName(), keyClassMap);
    }
}
