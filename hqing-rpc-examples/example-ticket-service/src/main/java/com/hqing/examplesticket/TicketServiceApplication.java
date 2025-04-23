package com.hqing.examplesticket;

import com.hqing.examplescommon.service.TicketService;
import com.hqing.examplescommon.service.UserService;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import com.hqing.hqrpc.model.ServiceLocalRegisterInfo;
import com.hqing.hqrpc.model.ServiceRegisterInfo;
import com.hqing.hqrpc.proxy.ServiceProxyFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 票务模块启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class TicketServiceApplication {
    public static void main(String[] args) throws Exception {
        //初始化Rpc框架服务提供者
        ProviderBootstrap.init();
        //构造服务注册信息(服务暴露信息+服务本地注册信息)
        ServiceRegisterInfo<TicketService> registerInfo = new ServiceRegisterInfo<>();
        registerInfo.setServiceName(TicketService.class.getName());

        //反射给内部字段设置代理对象
        TicketServiceImpl ticketServiceImpl = new TicketServiceImpl();
        Field userServiceFiled = TicketServiceImpl.class.getDeclaredField("userService");
        //设置强制调用
        userServiceFiled.setAccessible(true);
        userServiceFiled.set(ticketServiceImpl, ServiceProxyFactory.getProxy(UserService.class, "1.0"));
        userServiceFiled.setAccessible(false);
        registerInfo.setServiceLocalRegisterInfo(new ServiceLocalRegisterInfo<>(TicketServiceImpl.class, ticketServiceImpl));
        //注册到Rpc框架
        ProviderBootstrap.registerService(List.of(registerInfo));
    }
}
