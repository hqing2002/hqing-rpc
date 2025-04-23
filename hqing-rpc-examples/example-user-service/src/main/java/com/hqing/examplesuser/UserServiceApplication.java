package com.hqing.examplesuser;

import com.hqing.examplescommon.service.TicketService;
import com.hqing.examplescommon.service.UserService;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import com.hqing.hqrpc.model.ServiceLocalRegisterInfo;
import com.hqing.hqrpc.model.ServiceRegisterInfo;
import com.hqing.hqrpc.proxy.ServiceProxyFactory;

import java.util.List;

/**
 * 用户服务启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class UserServiceApplication {
    public static void main(String[] args) {
        //初始化Rpc框架服务提供者
        ProviderBootstrap.init();
        //构造服务注册信息(服务暴露信息+服务本地注册信息)
        ServiceRegisterInfo<UserService> registerInfo = new ServiceRegisterInfo<>();
        registerInfo.setServiceName(UserService.class.getName());
        registerInfo.setServiceLocalRegisterInfo(new ServiceLocalRegisterInfo<>(
                UserService.class,
                new UserServiceImpl()
        ));
        //将用户服务注册到Rpc框架
        ProviderBootstrap.registerService(List.of(registerInfo));

        //测试调用票务服务
        TicketService ticketService = ServiceProxyFactory.getProxy(TicketService.class, "1.0");
        int ticketPrice = ticketService.getTicketPrice(1L);
        System.out.println(ticketPrice);
        ticketPrice = ticketService.getTicketPrice(2L);
        System.out.println(ticketPrice);
        ticketPrice = ticketService.getTicketPrice(3L);
        System.out.println(ticketPrice);
    }
}
