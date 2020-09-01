package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/19 7:13 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class HallInfoVO implements Serializable {
    private String hallFieldId;

    private String hallName;

    private String price;

    private String seatFile;

    private String soldSeats;
}
