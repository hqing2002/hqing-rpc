package com.hqing.hqrpc.loadbalancer.impl;

import cn.hutool.core.collection.CollUtil;
import com.hqing.hqrpc.loadbalancer.LoadBalancer;
import com.hqing.hqrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {
    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;
    /**
     * 同步锁对象
     */
    private final Object lock = new Object();
    /**
     * 一致性哈希环, 使用volatile保证多线程可见
     */
    private volatile TreeMap<Integer, ServiceMetaInfo> hashCircle = new TreeMap<>();
    /**
     * 存储当前服务列表hash
     */
    private volatile int currentHash = 0;

    /**
     * Hash算法
     */
    private int getHash(Object key) {
        return key.hashCode();
    }

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            return null;
        }
        //获取当前服务列表对象的hash值
        int newHash = serviceMetaInfoList.hashCode();

        //缓存volatile变量hashCircle的当前引用, 避免多次访问主内存, 提升性能。
        TreeMap<Integer, ServiceMetaInfo> currentHashCircle = this.hashCircle;

        //服务列表哈希变化时, 使用双重检查锁同步重建环
        if (newHash != currentHash) {
            synchronized (lock) {
                if (newHash != currentHash) {
                    //获取新的哈希环
                    TreeMap<Integer, ServiceMetaInfo> newHashCircle = buildHashCircle(serviceMetaInfoList);
                    //更新服务列表hash
                    currentHash = newHash;
                    //更新哈希环
                    hashCircle = newHashCircle;
                    currentHashCircle = newHashCircle;
                }
            }
        }
        //获取请求调用的hash值
        int hash = getHash(requestParams);

        //ceilingEntry返回一个具有大于或等于key的最小键的条目, 相当于我们顺序针在环上寻找最接近且大于等于调用hash值的节点
        Map.Entry<Integer, ServiceMetaInfo> entry = currentHashCircle.ceilingEntry(hash);

        //如果没有大于等于调用请求hash值的虚拟节点, 返回环首部的节点, 模拟环状结构
        if (entry == null) {
            entry = currentHashCircle.firstEntry();
        }
        return entry != null ? entry.getValue() : null;
    }

    //构建哈希环
    private TreeMap<Integer, ServiceMetaInfo> buildHashCircle(List<ServiceMetaInfo> serviceMetaInfoList) {
        TreeMap<Integer, ServiceMetaInfo> newMap = new TreeMap<>();
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            //创建每个服务实例的虚拟节点
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                //获取节点Hash值
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                //放入哈希环中
                newMap.put(hash, serviceMetaInfo);
            }
        }
        return newMap;
    }
}
