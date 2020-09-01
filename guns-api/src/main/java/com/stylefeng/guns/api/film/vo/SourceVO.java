package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/18 7:29 上午
 * @Description 片源
 * @Verion 1.0
 */
@Data
public class SourceVO implements Serializable {
    private String sourceId;

    private String sourceName;

    private boolean isActive;
}
