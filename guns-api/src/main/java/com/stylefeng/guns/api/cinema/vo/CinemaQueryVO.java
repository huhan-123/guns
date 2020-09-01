package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/18 9:46 下午
 * @Description /cinema/getCinemas 接口入参
 * @Verion 1.0
 */
@Data
public class CinemaQueryVO implements Serializable {
    //影院编号
    private Integer brandId=99;

    //行政区编号
    private Integer districtId=99;

    //影厅类型
    private Integer hallType=99;

    private Integer pageSize = 12;

    private Integer nowPage =1;
}
