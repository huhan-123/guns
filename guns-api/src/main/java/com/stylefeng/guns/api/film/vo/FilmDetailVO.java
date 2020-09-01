package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/18 10:54 上午
 * @Description /film/films 接口返回所需对象
 * @Verion 1.0
 */
@Data
public class FilmDetailVO implements Serializable {
    private String filmId;
    private String filmName;
    private String filmEnName;
    private String imgAddress;
    private String score;
    private String scoreNum;
    private String totalBox;
    private String info01;
    private String info02;
    private String info03;
    private InfoRequestVO info04;
}
