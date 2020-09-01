package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/19 7:08 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class FilmInfoVO implements Serializable {
    private String filmId;

    private String filmName;

    private String filmLength;

    private String filmType;

    private String filmCats;

    private String actors;

    private String imgAddress;

    private List<FilmFieldVO> filmFields;
}
