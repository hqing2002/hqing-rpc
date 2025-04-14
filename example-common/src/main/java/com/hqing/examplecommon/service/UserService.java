package com.hqing.examplecommon.service;

import com.hqing.examplecommon.model.User;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public interface UserService {
    /**
     * 获取用户
     *
     * @param userName 用户的名称
     * @return 用户对象
     */
    User getUser(String userName);
}
