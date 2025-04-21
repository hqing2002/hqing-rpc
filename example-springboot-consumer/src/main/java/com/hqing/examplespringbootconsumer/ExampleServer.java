package com.hqing.examplespringbootconsumer;

import com.hqing.examplecommon.model.User;
import com.hqing.examplecommon.service.UserService;
import com.hqing.hqingrpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Service;

/**
 * 示例服务
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Service
public class ExampleServer {
    @RpcReference
    private UserService userService;

    public void testRpc() {
        User user = userService.getUser("hqing");
        if (user == null) {
            System.out.println("远程调用失败");
            return;
        }
        System.out.println(user);
    }
}
