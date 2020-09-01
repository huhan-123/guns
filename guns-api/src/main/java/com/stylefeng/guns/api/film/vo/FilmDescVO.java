package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/18 2:30 下午
 * @Description /film/films 接口返回所需对象
 * @Verion 1.0
 */
@Data
public class FilmDescVO implements Serializable {
    private String biography;

    private String filmId;
}
