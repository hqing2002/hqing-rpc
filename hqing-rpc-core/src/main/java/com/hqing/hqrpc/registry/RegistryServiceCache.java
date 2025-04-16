package com.hqing.hqrpc.registry;

import cn.hutool.core.collection.CollUtil;
import com.hqing.hqrpc.model.ServiceMetaInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心全局服务缓存(消费端)
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class RegistryServiceCache {
    /**
     * 全局服务缓存, 服务名称->(节点名称,服务元信息)
     * 使用服务键来定位服务, 使用节点键来定位具体的节点, 实现节点粒度的增删改查
     */
    Map<String, Map<String, ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 判断服务是否在全局缓存中
     *
     * @param serviceKey 服务键
     */
    public boolean containsService(String serviceKey) {
        return serviceCache.containsKey(serviceKey);
    }

    /**
     * 判断节点是否在全局缓存中
     *
     * @param serviceKey     服务键
     * @param serviceNodeKey 节点键
     */
    public boolean containsServiceNode(String serviceKey, String serviceNodeKey) {
        boolean serviceCacheExist = containsService(serviceKey);
        if (!serviceCacheExist) {
            return false;
        }
        return serviceCache.get(serviceKey).containsKey(serviceNodeKey);
    }

    /**
     * 写缓存
     *
     * @param serviceKey      服务的键
     * @param serviceNodeKey  节点的键
     * @param serviceMetaInfo 写入的服务的元信息
     */
    public void writeCache(String serviceKey, String serviceNodeKey, ServiceMetaInfo serviceMetaInfo) {
        //服务已在全局缓存中
        if (serviceCache.containsKey(serviceKey)) {
            //获取服务的节点缓存
            Map<String, ServiceMetaInfo> nodeCache = serviceCache.get(serviceKey);
            //将写入的缓存数据存入节点缓存map
            nodeCache.put(serviceNodeKey, serviceMetaInfo);
            return;
        }
        //服务未在全局缓存中,创建该服务的节点缓存map
        Map<String, ServiceMetaInfo> nodeCache = new ConcurrentHashMap<>();
        //将节点信息放入节点缓存map
        nodeCache.put(serviceNodeKey, serviceMetaInfo);
        //将服务放在全局缓存中
        serviceCache.put(serviceKey, nodeCache);
    }

    /**
     * 读缓存
     *
     * @param serviceKey 服务的键
     */
    public List<ServiceMetaInfo> readCache(String serviceKey) {
        //获取服务的节点缓存
        Map<String, ServiceMetaInfo> nodeCache = serviceCache.get(serviceKey);
        //如果没有数据就返回空集合
        if (CollUtil.isEmpty(nodeCache)) {
            return Collections.emptyList();
        }
        //将节点缓存中的服务元信息转为list返回
        return new ArrayList<>(nodeCache.values());
    }

    /**
     * 清空单个服务缓存
     *
     * @param serviceKey 服务的键
     */
    public void clearCache(String serviceKey) {
        serviceCache.remove(serviceKey);
    }

    /**
     * 移除具体节点缓存
     *
     * @param serviceKey     服务的键
     * @param serviceNodeKey 节点的键
     */
    public void removeNodeCache(String serviceKey, String serviceNodeKey) {
        Map<String, ServiceMetaInfo> nodeCache = serviceCache.get(serviceKey);
        if (CollUtil.isEmpty(nodeCache)) {
            return;
        }
        nodeCache.remove(serviceNodeKey);
    }
}
