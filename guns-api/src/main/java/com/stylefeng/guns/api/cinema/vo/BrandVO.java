package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/19 6:56 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class BrandVO implements Serializable {
    private String brandId;

    private String brandName;

    private boolean isActive;
}
