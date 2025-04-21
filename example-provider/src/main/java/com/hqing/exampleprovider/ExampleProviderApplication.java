package com.hqing.exampleprovider;

import com.hqing.examplecommon.service.UserService;
import com.hqing.hqrpc.bootstrap.ProviderBootstrap;
import com.hqing.hqrpc.model.ServiceRegisterInfo;
import lombok.extern.slf4j.Slf4j;

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
        //要注册的服务
        List<ServiceRegisterInfo<?>> registerInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>
                (UserService.class.getName(), "2.0", UserServiceImpl.class);
        registerInfoList.add(serviceRegisterInfo);
        //服务提供者初始化
        ProviderBootstrap.init(registerInfoList);
    }
}
