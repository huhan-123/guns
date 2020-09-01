package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/18 7:11 上午
 * @Description 电影类型
 * @Verion 1.0
 */
@Data
public class CatVO implements Serializable {
    private String  catId;

    //类型名
    private String catName;

    private boolean isActive;
}
