package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/18 7:30 上午
 * @Descriptio 电影年代
 * @Verion 1.0
 */
@Data
public class YearVO implements Serializable {
    private String yearId;

    private String yearName;

    private boolean isActive;
}
