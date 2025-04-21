package com.hqing.hqrpc;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * FileDescribe
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class AtomicTest {
    /**
     * 使用原子类保证只开启一次心跳检测
     */
    private static final AtomicBoolean FIRST_START = new AtomicBoolean(true);

    public static int test() {
        int a = 1;
        if(FIRST_START.get()) {
            a = 2;
            FIRST_START.set(false);
        }
        return a;
    }

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(AtomicTest.test());
            }
        });
        thread.start();
    }
}
