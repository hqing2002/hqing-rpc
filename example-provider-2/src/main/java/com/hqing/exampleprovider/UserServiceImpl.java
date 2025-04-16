package com.hqing.exampleprovider;

import com.hqing.examplecommon.model.User;
import com.hqing.examplecommon.service.UserService;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(String userName) {
        System.out.println("用户名: " + userName);
        return new User(userName);
    }
}
