package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/19 7:10 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class FilmFieldVO implements Serializable {
    private String fieldId;

    private String beginTime;

    private String endTime;

    private String language;

    private String hallName;

    private String price;
}
