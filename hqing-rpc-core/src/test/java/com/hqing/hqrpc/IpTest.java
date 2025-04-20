package com.hqing.hqrpc;

import cn.hutool.core.net.NetUtil;

import java.net.InetAddress;

/**
 * FileDescribe
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class IpTest {
    public static void main(String[] args) {
        InetAddress localhost = NetUtil.getLocalhost();
        System.out.println(localhost.getHostAddress());
    }
}
