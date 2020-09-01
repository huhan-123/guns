package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/19 6:57 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class AreaVO implements Serializable {
    private String areaId;

    private String areaName;

    private boolean isActive;
}
