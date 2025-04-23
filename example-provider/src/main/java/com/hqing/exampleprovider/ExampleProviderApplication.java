package com.hqing.exampleprovider;

import com.hqing.examplecommon.service.TimeService;
import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import com.hqing.hqrpc.model.ServiceLocalRegisterInfo;
import com.hqing.hqrpc.model.ServiceRegisterInfo;
import com.hqing.hqrpc.proxy.ServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者启动类
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Slf4j
public class ExampleProviderApplication {
    public static void main(String[] args) {
        //构造用户服务实现类实例
        UserServiceImpl userServiceImpl;
        try {
            userServiceImpl = new UserServiceImpl();
            Field timeService = UserServiceImpl.class.getDeclaredField("timeService");
            timeService.setAccessible(true);
            timeService.set(userServiceImpl, ServiceProxyFactory.getProxy(TimeService.class, "1.0"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //要注册的服务
        List<ServiceRegisterInfo<?>> registerInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>
                (UserService.class.getName(), "1.0", new ServiceLocalRegisterInfo<>(UserServiceImpl.class, userServiceImpl));
        registerInfoList.add(serviceRegisterInfo);
        //服务提供者初始化
        ProviderBootstrap.init();
        //注册服务
        ProviderBootstrap.registerService(registerInfoList);
    }
}
