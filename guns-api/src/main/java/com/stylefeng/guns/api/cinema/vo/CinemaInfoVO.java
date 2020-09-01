package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/19 7:05 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class CinemaInfoVO implements Serializable {
    private String cinemaId;

    private String imgUrl;

    private String cinemaName;

    private String cinemaAdress;

    private String cinemaPhone;
}
