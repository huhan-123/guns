package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/17 8:34 下午
 * @Description 电影信息
 * @Verion 1.0
 */
@Data
public class FilmInfo implements Serializable {
    private String filmId;

    //电影类型（0-2D，1-3D，2-3DIMAX，4-无）
    private int filmType;

    //电影主题图片地址
    private String imgAddress;

    //电影名
    private String filmName;

    //电影评分
    private String filmScore;

    //预售数目
    private int expectNum;

    //上映时间
    private String showTime;

    //电影票房
    private int boxNum;

    //电影评分
    private String score;
}
