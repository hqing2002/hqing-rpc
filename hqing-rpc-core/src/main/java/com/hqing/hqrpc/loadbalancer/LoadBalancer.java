package com.hqing.hqrpc.loadbalancer;

import com.hqing.hqrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface LoadBalancer {
    /**
     * 选择服务调用
     *
     * @param requestParams       请求参数
     * @param serviceMetaInfoList 服务列表
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
