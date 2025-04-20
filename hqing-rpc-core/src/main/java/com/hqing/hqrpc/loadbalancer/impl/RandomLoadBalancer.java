package com.hqing.hqrpc.loadbalancer.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.hqing.hqrpc.loadbalancer.LoadBalancer;
import com.hqing.hqrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 随机负载均衡器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class RandomLoadBalancer implements LoadBalancer {
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
        //取随机数下标
        int index = RandomUtil.randomInt(size);
        return serviceMetaInfoList.get(index);
    }
}
