package com.hqing.hqrpc.loadbalancer.impl;

import cn.hutool.core.collection.CollUtil;
import com.hqing.hqrpc.loadbalancer.LoadBalancer;
import com.hqing.hqrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    /**
     * 记录当前轮询下标, 使用原子类防止并发冲突
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        //获得元素个数
        int size = CollUtil.size(serviceMetaInfoList);
        if (size == 0) {
            return null;
        }
        //如果只有一个服务,直接返回
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        //取模获取下标
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
