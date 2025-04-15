package com.hqing.examplecommon.model;

import java.io.Serializable;

/**
 * 用户模型
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
public class User implements Serializable {
    private String userName;

    private Long count;

    private Integer age;

    public User(String userName) {
        this.userName = userName;
        this.count = 10000L;
        this.age = 18;
    }

    public User() {
        this.userName = "default";
        this.count = 0L;
        this.age = 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", count=" + count +
                ", age=" + age +
                '}';
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
