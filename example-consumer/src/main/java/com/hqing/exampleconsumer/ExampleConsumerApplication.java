package com.hqing.exampleconsumer;

import com.hqing.examplecommon.model.User;
import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.bootstrap.ConsumerBootstrap;
import com.hqing.hqrpc.proxy.ServiceProxyFactory;

/**
 * 消费服务启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ExampleConsumerApplication {
    public static void main(String[] args) {
        ConsumerBootstrap.init();

        //rpc获取用户服务实现类
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = userService.getUser("hqing");
        User user1 = userService.getUser("hqing1");
        User user2 = userService.getUser("hqing2");
        User user3 = userService.getUser("hqing3");
        if (user == null || user1 == null || user2 == null || user3 == null) {
            System.out.println("远程调用失败");
            return;
        }
        System.out.println(user);
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
    }
}
