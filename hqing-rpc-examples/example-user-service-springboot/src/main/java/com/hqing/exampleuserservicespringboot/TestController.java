package com.hqing.exampleuserservicespringboot;

import com.hqing.examplescommon.service.TicketService;
import com.hqing.hqingrpcspringbootstarter.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试RPC框架
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@RestController
public class TestController {
    @RpcReference
    TicketService ticketService;

    @GetMapping("/getPrice/{id}")
    public int getPrice(@PathVariable("id") Long id) {
        return ticketService.getTicketPrice(id);
    }
}
