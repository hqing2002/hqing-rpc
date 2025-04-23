package com.hqing.examplescommon.service;

import com.hqing.examplescommon.model.User;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface UserService {
    /**
     * 根据ID获取用户对象
     * @param userId 用户id
     * @return 用户对象
     */
    User getUser(Long userId);
}
