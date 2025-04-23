package com.hqing.exampleticketservicespringboot;

import com.hqing.examplescommon.model.User;
import com.hqing.examplescommon.service.TicketService;
import com.hqing.examplescommon.service.UserService;
import com.hqing.hqingrpcspringbootstarter.annotation.RpcReference;
import com.hqing.hqingrpcspringbootstarter.annotation.RpcService;

/**
 * 票务模块实现类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@RpcService(serviceVersion = "2.0")
public class TicketServiceImplVersion2 implements TicketService {
    @RpcReference
    private UserService userService;

    @Override
    public int getTicketPrice(Long userId) {
        User user = userService.getUser(userId);
        System.out.println("用户信息: " + user);
        System.out.println("不管你年龄多少都80");
        return 80;
    }
}
