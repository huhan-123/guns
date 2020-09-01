package com.stylefeng.guns.api.film.api;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/17 8:59 下午
 * @Description
 * @Verion 1.0
 */
public interface FilmServiceAPI {
    //获取 banners
    List<BannerVO> getBanners();

    //获取热映影片
    FilmVO getHotFilms(boolean isLimit, int num, int nowPage, int sortId, int sourceId, int yearId, int catId);

    //获取即将上映的影片（受欢迎程度做排序）
    FilmVO getSoonFilms(boolean isLimit, int num, int nowPage, int sortId, int sourceId, int yearId, int catId);

    //获取经典影片
    FilmVO getClassicFilms(int num, int nowPage, int sortId, int sourceId, int yearId, int catId);

    //获取票房排行榜
    List<FilmInfo> getBoxRanking();

    //获取人气排行榜
    List<FilmInfo> getExpectRanking();

    //获取 Top100
    List<FilmInfo> getTop();

    //===== 获取影片条件接口 ====
    //分类条件
    List<CatVO> getCats();

    //片源条件
    List<SourceVO> getSources();

    //年代条件
    List<YearVO> getYears();

    //根据影片 ID 或者名称获取影片信息
    FilmDetailVO getFilmDetail(int searchType, String searchParam);

    //获取影片的描述信息
    FilmDescVO getFilmDesc(String filmId);

    //获取图片信息
    ImgVO getImgs(String filmId);

    //获取导演信息
    ActorVO getDectInfo(String filmId);

    //获取演员信息
    List<ActorVO> getActors(String filmIs);


}
