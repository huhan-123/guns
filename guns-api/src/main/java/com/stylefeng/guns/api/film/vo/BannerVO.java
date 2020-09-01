package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/17 8:15 下午
 * @Description
 * @Verion 1.0
 */
@Data
public class BannerVO implements Serializable {
    private String bannerId;

    //banner图存放路径
    private String bannerAddress;

    //banner点击跳转url
    private String bannerUrl;
}
