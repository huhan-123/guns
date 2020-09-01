package com.stylefeng.guns.api.film.vo;

import com.stylefeng.guns.api.film.vo.FilmInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/17 8:45 下午
 * @Description
 * @Verion 1.0
 */
@Data
public class FilmVO implements Serializable {
    private int filmNum;

    private List<FilmInfo> filmInfo;

    private int totalPage;

    private int nowPage;
}
