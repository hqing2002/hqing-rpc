package com.hqing.examplespringbootconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ExampleSpringbootConsumerApplicationTests {
    @Resource
    private ExampleServer exampleServer;

    @Test
    void contextLoads() {
        exampleServer.testRpc();
    }
}
