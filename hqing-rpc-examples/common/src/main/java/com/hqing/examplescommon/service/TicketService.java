package com.hqing.examplescommon.service;

/**
 * 票务服务
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface TicketService {
    /**
     * 根据用户ID计算票价
     *
     * @param userId 用户id
     * @return 票价描述
     */
    int getTicketPrice(Long userId);
}
