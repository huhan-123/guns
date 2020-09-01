package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/19 7:04 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class HallTypeVO implements Serializable {
    private String halltypeId;

    private String halltypeName;

    private boolean isActive;
}
