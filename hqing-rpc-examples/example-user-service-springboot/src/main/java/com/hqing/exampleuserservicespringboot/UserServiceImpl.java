package com.hqing.exampleuserservicespringboot;

import com.hqing.examplescommon.model.User;
import com.hqing.examplescommon.service.UserService;
import com.hqing.hqingrpcspringbootstarter.annotation.RpcService;

import java.util.Map;

/**
 * 用户服务实现类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@RpcService
public class UserServiceImpl implements UserService {
    /**
     * 模拟数据库存储
     */
    private final Map<Long, User> userMap = Map.of(
            1L, new User(1L, 25),
            2L, new User(2L, 12),
            3L, new User(3L, 66)
    );

    @Override
    public User getUser(Long userId) {
        System.out.println("请求参数: " + userId);
        return userMap.get(userId);
    }
}
