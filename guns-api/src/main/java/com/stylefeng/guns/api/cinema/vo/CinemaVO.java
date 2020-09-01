package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/19 6:55 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class CinemaVO implements Serializable {
    private String uuid;

    private String cinemaName;

    private String address;

    private String minimumPrice;
}
