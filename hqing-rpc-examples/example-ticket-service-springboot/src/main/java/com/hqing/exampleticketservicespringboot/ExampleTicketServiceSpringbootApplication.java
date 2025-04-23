package com.hqing.exampleticketservicespringboot;

import com.hqing.hqingrpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRpc
@SpringBootApplication
public class ExampleTicketServiceSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleTicketServiceSpringbootApplication.class, args);
    }

}
