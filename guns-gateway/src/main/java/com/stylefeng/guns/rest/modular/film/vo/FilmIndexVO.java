package com.stylefeng.guns.rest.modular.film.vo;

import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import lombok.Data;

import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/17 8:50 下午
 * @Description 首页返回对象
 * @Verion 1.0
 */
@Data
public class FilmIndexVO {
    private List<BannerVO> banners;

    private FilmVO hotFilms;

    private FilmVO soonFilms;

    private List<FilmInfo> boxRanking;

    private List<FilmInfo> expectRanking;

    private List<FilmInfo> top100;
}
