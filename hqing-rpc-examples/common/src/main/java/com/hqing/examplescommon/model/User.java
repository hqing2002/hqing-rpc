package com.hqing.examplescommon.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户模型
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Data
@AllArgsConstructor
public class User implements Serializable {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户年龄
     */
    private Integer age;
}
