package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/18 3:30 下午
 * @Description
 * @Verion 1.0
 */
@Data
public class InfoRequestVO implements Serializable {
    private String biography;
    private ActorRequestVO actors;
    private ImgVO imgs;
    private String filmId;
}
