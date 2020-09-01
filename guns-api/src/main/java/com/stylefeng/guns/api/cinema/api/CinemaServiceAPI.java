package com.stylefeng.guns.api.cinema.api;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.vo.*;

import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/18 9:45 下午
 * @Description
 * @Verion 1.0
 */
public interface CinemaServiceAPI {
    //根据 CinemaQueryVO，查询影院列表
    Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO);

    //根据条件获取品牌列表
    List<BrandVO> getBrands(int brandId);

    //获取行政区域列表
    List<AreaVO> getAreas(int areaId);

    //获取影厅类型列表
    List<HallTypeVO> getHallTypes(int hallType);

    //根据影院编号，获取影院信息
    CinemaInfoVO getCinemaInfoById(int cinemeId);

    //获取所有电影的信息和对应的放映场次信息，根据影院编号
    List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId);


    //根据放映场次 id 获取放映信息
    HallInfoVO getFilmFieldInfo(int fieldId);

    //根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    FilmInfoVO getFilmInfoByFieldId(int fieldId);

    /*
        该部分是订单模块需要的内容
     */
    OrderQueryVO getOrderNeeds(int fieldId);
}
