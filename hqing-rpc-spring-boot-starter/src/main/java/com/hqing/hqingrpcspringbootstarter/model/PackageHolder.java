package com.hqing.hqingrpcspringbootstarter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储扫描包路径
 *
 * @author <a href="https://github.com/hqing2002">Hqing</a>
 */
@Getter
@AllArgsConstructor
public class PackageHolder {
    private final String[] basePackages;
}
