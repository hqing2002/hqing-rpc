package com.hqing.examplespringbootprovider;

import com.hqing.examplecommon.model.User;
import com.hqing.examplecommon.service.UserService;
import com.hqing.hqingrpcspringbootstarter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(String userName) {
        System.out.println("用户名: " + userName);
        return new User(userName);
    }
}
