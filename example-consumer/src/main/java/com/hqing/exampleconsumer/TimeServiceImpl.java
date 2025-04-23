package com.hqing.exampleconsumer;

import com.hqing.examplecommon.service.TimeService;

import java.time.LocalDateTime;

/**
 * FileDescribe
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class TimeServiceImpl implements TimeService {
    @Override
    public String getTime() {
        System.out.println("服务消费者时间提供" + LocalDateTime.now());
        return LocalDateTime.now().toString();
    }
}
