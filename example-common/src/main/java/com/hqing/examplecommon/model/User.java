package com.hqing.examplecommon.model;

import java.io.Serializable;

/**
 * 用户模型
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class User implements Serializable {
    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
