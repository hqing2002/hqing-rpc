package com.hqing.exampleconsumer;

import com.hqing.examplecommon.model.User;
import com.hqing.examplecommon.service.TimeService;
import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import com.hqing.hqrpc.model.ServiceLocalRegisterInfo;
import com.hqing.hqrpc.model.ServiceRegisterInfo;
import com.hqing.hqrpc.proxy.ServiceProxyFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费服务启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class ExampleConsumerApplication {
    public static void main(String[] args) {
        //要注册的服务
        List<ServiceRegisterInfo<?>> registerInfoList = new ArrayList<>();
        ServiceRegisterInfo<TimeService> serviceRegisterInfo = new ServiceRegisterInfo<>
                (TimeService.class.getName(), new ServiceLocalRegisterInfo<>(TimeServiceImpl.class, new TimeServiceImpl()));
        registerInfoList.add(serviceRegisterInfo);
        //服务注册
        ProviderBootstrap.init();
        ProviderBootstrap.registerService(registerInfoList);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //rpc获取用户服务实现类
        UserService userService = ServiceProxyFactory.getProxy(UserService.class, "1.0");
        User user = userService.getUser("hqing");
        System.out.println(user);
    }
}
