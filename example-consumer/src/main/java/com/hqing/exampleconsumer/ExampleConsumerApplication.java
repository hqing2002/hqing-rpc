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
    public static void main(String[] args) {
        //rpc获取用户服务实现类
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        String userName = "hqing";
        User user = userService.getUser(userName);
        if (user == null) {
            System.out.println("远程调用失败");
            return;
        }
        System.out.println(user.getUserName());
    }
}
