package com.hqing.examplespringbootprovider;

import com.hqing.hqingrpcspringbootstarter.annotation.RpcReference;
import com.hqing.hqingrpcspringbootstarter.annotation.RpcService;
import com.hqing.hqingrpcspringbootstarter.model.PackageHolder;
import com.hqing.hqingrpcspringbootstarter.utils.AnnotationScannerUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Map;

@SpringBootTest
class ExampleSpringbootProviderApplicationTests {
    @Resource
    PackageHolder packageHolder;

    @Test
    void contextLoads() {
        Map<Class<?>, AnnotationScannerUtil.AnnotatedInfo> classAnnotatedInfoMap = AnnotationScannerUtil.scanPackageWithAnnotation(packageHolder.getBasePackages(), RpcService.class, RpcReference.class);
        System.out.println(classAnnotatedInfoMap);
    }
}
