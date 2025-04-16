package com.hqing.exampleconsumer;

import com.hqing.examplecommon.model.User;
import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.proxy.ServiceProxyFactory;

/**
 * 消费服务启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ExampleConsumerApplication {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("123");
        User user = getUser();
        System.out.println("123");
        System.out.println(user);
    }

    public static User getUser () {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        return userService.getUser("hqing");
    }
}
