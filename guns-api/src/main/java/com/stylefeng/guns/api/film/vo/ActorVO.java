package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/18 2:34 下午
 * @Description
 * @Verion 1.0
 */
@Data
public class ActorVO implements Serializable {
    private String imgAddress;

    private String directorName;

    private String roleName;
}
