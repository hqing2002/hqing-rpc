package com.hqing.exampleuserservicespringboot;

import com.hqing.hqingrpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRpc
@SpringBootApplication
public class ExampleUserServiceSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleUserServiceSpringbootApplication.class, args);
    }

}
