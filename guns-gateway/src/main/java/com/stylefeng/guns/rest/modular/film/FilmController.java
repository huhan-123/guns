package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.api.FilmAsyncServiceAPI;
import com.stylefeng.guns.api.film.api.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author: huhan
 * @Date 2020/8/17 3:08 下午
 * @Description 电影相关接口
 * @Verion 1.0
 */
@RestController
@RequestMapping("/film")
public class FilmController {
    private static final String IMG_PRE = "http://img.meetingshop.cn";
    @Reference(interfaceClass = FilmServiceAPI.class)
    private FilmServiceAPI filmServiceAPI;

    //这里设置 async=true，那么 FilmAsyncServiceAPI 里面所有的方法都会使用异步调用，只能得到 future
    @Reference(interfaceClass = FilmAsyncServiceAPI.class, async = true)
    private FilmAsyncServiceAPI filmAsyncServiceAPI;

    /**
     * 获取首页
     * API 网关：
     * 1.功能聚合（API 聚合）
     * 好处：
     * 1.六个接口，一次请求，同一时刻节省了五次 HTTP 请求
     * 2.同一个接口对外暴露，降低了前后端分离开发的难度和复杂度
     * 坏处：
     * 1.一次获取数据过多，容易出现问题
     */
    //获取首页
    @GetMapping("/getIndex")
    public ResponseVO<FilmIndexVO> getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        //获取 banner 信息
        filmIndexVO.setBanners(filmServiceAPI.getBanners());

        //获取热映电影列表
        filmIndexVO.setHotFilms(filmServiceAPI.getHotFilms(true, 8, 1, 1, 99, 99, 99));

        //获取即将上映的电影列表
        filmIndexVO.setSoonFilms(filmServiceAPI.getSoonFilms(true, 8, 1, 1, 99, 99, 99));

        //获取票房排行榜
        filmIndexVO.setBoxRanking(filmServiceAPI.getBoxRanking());

        //获取受欢迎的榜单
        filmIndexVO.setExpectRanking(filmServiceAPI.getExpectRanking());

        //获取 top100 的电影
        filmIndexVO.setTop100(filmServiceAPI.getTop());
        return ResponseVO.success(IMG_PRE, filmIndexVO);
    }

    //获取条件列表
    @GetMapping("/getConditionList")
    public ResponseVO<FilmConditionVO> getConditionList(@RequestParam(value = "catId", required = false, defaultValue = "99") String catId,
                                                        @RequestParam(value = "sourceId", required = false, defaultValue = "99") String sourceId,
                                                        @RequestParam(value = "yearId", required = false, defaultValue = "99") String yearId) {
        FilmConditionVO filmConditionVO = new FilmConditionVO();
        boolean flag = false;
        //类型集合
        List<CatVO> cats = filmServiceAPI.getCats();
        List<CatVO> catResult = new ArrayList<>();
        CatVO catVO = null;
        for (CatVO cat : cats) {
            //判断集合是否存在 catId，如果存在，则将对应的实体变成 active 状态
            if (cat.getCatId().equals("99")) {
                catVO = cat;
                continue;
            }
            if (cat.getCatId().equals(catId)) {
                flag = true;
                cat.setActive(true);
            } else {
                cat.setActive(false);
            }
            catResult.add(cat);
        }
        //如果不存在，则默认将全部变为 Active 状态
        if (!flag) {
            catVO.setActive(true);
            catResult.add(catVO);
        } else {
            catVO.setActive(false);
            catResult.add(catVO);
        }

        //片源集合
        flag = false;
        List<SourceVO> sources = filmServiceAPI.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        SourceVO sourceVO = null;
        for (SourceVO source : sources) {
            //判断集合是否存在 catId，如果存在，则将对应的实体变成 active 状态
            if (source.getSourceId().equals("99")) {
                sourceVO = source;
                continue;
            }
            if (source.getSourceId().equals(catId)) {
                flag = true;
                source.setActive(true);
            } else {
                source.setActive(false);
            }
            sourceResult.add(source);
        }
        if (!flag) {
            sourceVO.setActive(true);
            sourceResult.add(sourceVO);
        } else {
            sourceVO.setActive(false);
            sourceResult.add(sourceVO);
        }

        //年代集合
        flag = false;
        List<YearVO> years = filmServiceAPI.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        YearVO yearVO = null;
        for (YearVO year : years) {
            //判断集合是否存在 catId，如果存在，则将对应的实体变成 active 状态
            if (year.getYearId().equals("99")) {
                yearVO = year;
                continue;
            }
            if (year.getYearId().equals(catId)) {
                flag = true;
                year.setActive(true);
            } else {
                year.setActive(false);
            }
            yearResult.add(year);
        }
        if (!flag) {
            yearVO.setActive(true);
            yearResult.add(yearVO);
        } else {
            yearVO.setActive(false);
            yearResult.add(yearVO);
        }

        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);
        return ResponseVO.success(filmConditionVO);
    }

    @GetMapping("/getFilms")
    //根据条件筛选电影
    public ResponseVO<List<FilmInfo>> getFilms(FilmRequestVO filmRequestVO) {
        FilmVO filmVO = null;
        switch (filmRequestVO.getShowType()) {
            case 1:
                filmVO = filmServiceAPI.getHotFilms(false,
                        filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(),
                        filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            case 2:
                filmVO = filmServiceAPI.getSoonFilms(false,
                        filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(),
                        filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            case 3:
                filmVO = filmServiceAPI.getClassicFilms(
                        filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(),
                        filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            default:
                filmVO = filmServiceAPI.getHotFilms(false,
                        filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(),
                        filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
        }

        return ResponseVO.success(filmVO.getNowPage(), filmVO.getTotalPage(), IMG_PRE, filmVO.getFilmInfo());
    }

    @GetMapping("/films/{searchParam}")
    //获取影片的详细信息
    public ResponseVO films(@PathVariable("searchParam") String searchParam, int searchType) throws ExecutionException, InterruptedException {
        //根据 searchType，判断查询类型（1-根据名称查询，2-根据 id 查询）
        FilmDetailVO filmDetail = filmServiceAPI.getFilmDetail(searchType, searchParam);

        if (filmDetail == null || filmDetail.getFilmId() == null || StringUtils.isEmpty(filmDetail.getFilmId())) {
            return ResponseVO.serviceFail("没有可查询的影片");
        }

        String filmId = filmDetail.getFilmId();

        //查询影片的详细信息 -> Dubbo 的异步调用
        //获取影片的描述信息
        filmAsyncServiceAPI.getFilmDesc(filmId);
        Future<FilmDescVO> filmDescFuture = RpcContext.getContext().getFuture();

        //获取图片信息
        filmAsyncServiceAPI.getImgs(filmId);
        Future<ImgVO> imgVoFurure = RpcContext.getContext().getFuture();

        //获取导演信息
        filmAsyncServiceAPI.getDectInfo(filmId);
        Future<ActorVO> directorVoFuture = RpcContext.getContext().getFuture();

        //获取演员信息
        filmAsyncServiceAPI.getActors(filmId);
        Future<List<ActorVO>> actorsFuture = RpcContext.getContext().getFuture();

        InfoRequestVO infoRequestVO = new InfoRequestVO();
        //组织 Actor 属性
        ActorRequestVO actorRequestVO = new ActorRequestVO();
        actorRequestVO.setActors(actorsFuture.get());
        actorRequestVO.setDirector(directorVoFuture.get());

        //组织 info 对象
        infoRequestVO.setActors(actorRequestVO);
        infoRequestVO.setBiography(filmDescFuture.get().getBiography());
        infoRequestVO.setFilmId(filmId);
        infoRequestVO.setImgs(imgVoFurure.get());

        //组织成返回值
        filmDetail.setInfo04(infoRequestVO);
        return ResponseVO.success(IMG_PRE, filmDetail);
    }
}
