package com.hqing.examplesticket;

import com.hqing.examplescommon.model.User;
import com.hqing.examplescommon.service.TicketService;
import com.hqing.examplescommon.service.UserService;

/**
 * 票务模块实现类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class TicketServiceImpl implements TicketService {
    private UserService userService;

    @Override
    public int getTicketPrice(Long userId) {
        User user = userService.getUser(userId);
        System.out.println("用户信息: " + user);
        Integer age = user.getAge();
        if (age < 14) {
            System.out.println("儿童票半价40");
            return 40;
        }
        System.out.println("成人票80");
        return 80;
    }
}
