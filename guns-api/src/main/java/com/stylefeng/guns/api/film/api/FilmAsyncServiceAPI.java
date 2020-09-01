package com.stylefeng.guns.api.film.api;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/17 8:59 下午
 * @Description
 * @Verion 1.0
 */
//需要异步调用的方法要放到这个接口里面
public interface FilmAsyncServiceAPI {
    //获取影片的描述信息
    FilmDescVO getFilmDesc(String filmId);

    //获取图片信息
    ImgVO getImgs(String filmId);

    //获取导演信息
    ActorVO getDectInfo(String filmId);

    //获取演员信息
    List<ActorVO> getActors(String filmIs);


}
