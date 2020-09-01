package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;

/**
 * @Author: huhan
 * @Date 2020/8/18 9:02 上午
 * @Description /film/getFilms接口入参对象
 * @Verion 1.0
 */
@Data
public class FilmRequestVO {
    //查询类型，1-正在热映，2-即将上映，3-经典影片
    private Integer showType = 1;

    //排序方式，1-按热门搜排序，2-按时间排序，3-按评价排序
    private Integer sortId = 1;

    //类型编号
    private Integer catId = 99;

    //区域编号
    private Integer sourceId = 99;

    //年代编号
    private Integer yearId = 99;

    //影片列表当前页
    private Integer nowPage = 1;

    //每页显示条数
    private Integer pageSize = 18;
}
