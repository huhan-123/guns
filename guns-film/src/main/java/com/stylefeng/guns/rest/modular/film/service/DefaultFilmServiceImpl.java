package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.api.film.api.FilmServiceAPI;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/17 9:19 下午
 * @Description
 * @Verion 1.0
 */
@Component
@Service(interfaceClass = FilmServiceAPI.class)
public class DefaultFilmServiceImpl implements FilmServiceAPI {
    @Autowired
    private MoocBannerTMapper moocBannerTMapper;

    @Autowired
    private MoocFilmTMapper moocFilmTMapper;

    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;

    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;

    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;

    @Autowired
    private MoocFilmInfoTMapper moocFilmInfoTMapper;

    @Autowired
    private MoocActorTMapper moocActorTMapper;

    @Override
    public List<BannerVO> getBanners() {
        ArrayList<BannerVO> result = new ArrayList<>();
        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);

        for (MoocBannerT moocBanner : moocBanners) {
            BannerVO bannerVO = new BannerVO();
            bannerVO.setBannerId(moocBanner.getUuid() + "");
            bannerVO.setBannerAddress(moocBanner.getBannerAddress());
            bannerVO.setBannerUrl(moocBanner.getBannerUrl());
            result.add(bannerVO);
        }
        return result;
    }

    //首页板块的热门影片展示个数有限制，而电影板块里面的热门影片没有限制，所以这里用一个 limit 表示是首页板块还是电影板块
    @Override
    public FilmVO getHotFilms(boolean isLimit, int num, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();

        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", "1");
        if (isLimit) {//首页热门影片
            Page<MoocFilmT> page = new Page<>(1, num);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmInfo(filmInfos);
            filmVO.setFilmNum(filmInfos.size());
        } else {
            Page<MoocFilmT> page = null;
            //根据 sortId 的不同，来组织不同的 page 对象
            //排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
            switch (sortId) {
                case 1:
                    page = new Page<>(nowPage, num, "film_box_office");
                    break;
                case 2:
                    page = new Page<>(nowPage, num, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, num, "film_score");
                    break;
                default:
                    page = new Page<>(nowPage, num, "film_box_office");
                    break;
            }

            //如果 sourceId，yearId，catId 不为 99，则表示要按照对应的编号进行查询
            if (sourceId != 99) {
                wrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                wrapper.eq("film_date", yearId);
            }
            if (catId != 99) {
                String catStr = "%#" + catId + "#%";
                wrapper.like("film_cats", catStr);
            }
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
            //组织 filmInfos
            filmInfos = getFilmInfos(moocFilms);
            int totalCounts = moocFilmTMapper.selectCount(wrapper);
            int totalPages = totalCounts / num + 1;
            filmVO.setFilmInfo(filmInfos);
            filmVO.setFilmNum(filmInfos.size());
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int num, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();

        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", "2");
        if (isLimit) {//首页即将上映的影片
            Page<MoocFilmT> page = new Page<>(1, num);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmInfo(filmInfos);
            filmVO.setFilmNum(filmInfos.size());
        } else {
            Page<MoocFilmT> page = null;
            //根据 sortId 的不同，来组织不同的 page 对象
            //排序方式，1-按热门排序，2-按时排序，3-按评价排序
            switch (sortId) {
                case 1:
                    page = new Page<>(nowPage, num, "film_preSaleNum");
                    break;
                case 2:
                    page = new Page<>(nowPage, num, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, num, "film_preSaleNum");
                    break;
                default:
                    page = new Page<>(nowPage, num, "film_preSaleNum");
                    break;
            }

            //如果 sourceId，yearId，catId 不为 99，则表示要按照对应的编号进行查询
            if (sourceId != 99) {
                wrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                wrapper.eq("film_date", yearId);
            }
            if (catId != 99) {
                String catStr = "%#" + catId + "#%";
                wrapper.like("film_cats", catStr);
            }
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
            //组织 filmInfos
            filmInfos = getFilmInfos(moocFilms);
            int totalCounts = moocFilmTMapper.selectCount(wrapper);
            int totalPages = totalCounts / num + 1;
            filmVO.setFilmInfo(filmInfos);
            filmVO.setFilmNum(filmInfos.size());
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }

    @Override
    public FilmVO getClassicFilms(int num, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();

        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", "3");

        Page<MoocFilmT> page = null;
        //根据 sortId 的不同，来组织不同的 page 对象
        //排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
        switch (sortId) {
            case 1:
                page = new Page<>(nowPage, num, "film_box_office");
                break;
            case 2:
                page = new Page<>(nowPage, num, "film_time");
                break;
            case 3:
                page = new Page<>(nowPage, num, "film_score");
                break;
            default:
                page = new Page<>(nowPage, num, "film_box_office");
                break;
        }

        //如果 sourceId，yearId，catId 不为 99，则表示要按照对应的编号进行查询
        if (sourceId != 99) {
            wrapper.eq("film_source", sourceId);
        }
        if (yearId != 99) {
            wrapper.eq("film_date", yearId);
        }
        if (catId != 99) {
            String catStr = "%#" + catId + "#%";
            wrapper.like("film_cats", catStr);
        }
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
        //组织 filmInfos
        filmInfos = getFilmInfos(moocFilms);
        int totalCounts = moocFilmTMapper.selectCount(wrapper);
        int totalPages = totalCounts / num + 1;
        filmVO.setFilmInfo(filmInfos);
        filmVO.setFilmNum(filmInfos.size());
        filmVO.setTotalPage(totalPages);
        filmVO.setNowPage(nowPage);
        return null;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        //正在上映，票房前十名的电影
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", "1");
        Page<MoocFilmT> page = new Page<>(1, 10, "film_box_office");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
        return getFilmInfos(moocFilms);
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        //即将上映，预售前十名的电影
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", "2");
        Page<MoocFilmT> page = new Page<>(1, 10, "film_preSaleNum");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
        return getFilmInfos(moocFilms);
    }

    @Override
    public List<FilmInfo> getTop() {
        //正在上映的，评分前十名
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", "1");
        Page<MoocFilmT> page = new Page<>(1, 10, "film_score");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
        return getFilmInfos(moocFilms);
    }

    @Override
    public List<CatVO> getCats() {
        List<CatVO> cats = new ArrayList<>();
        List<MoocCatDictT> moocCatDicts = moocCatDictTMapper.selectList(null);

        for (MoocCatDictT moocCatDict : moocCatDicts) {
            CatVO catVO = new CatVO();
            catVO.setCatId(moocCatDict.getUuid() + "");
            catVO.setCatName(moocCatDict.getShowName());
            cats.add(catVO);
        }

        return cats;
    }

    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> sources = new ArrayList<>();

        List<MoocSourceDictT> moocSources = moocSourceDictTMapper.selectList(null);
        for (MoocSourceDictT moocSource : moocSources) {
            SourceVO sourceVO = new SourceVO();
            sourceVO.setSourceId(moocSource.getUuid() + "");
            sourceVO.setSourceName(moocSource.getShowName());
            sources.add(sourceVO);
        }

        return sources;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> years = new ArrayList<>();
        List<MoocYearDictT> moocYears = moocYearDictTMapper.selectList(null);
        for (MoocYearDictT moocYear : moocYears) {
            YearVO yearVO = new YearVO();
            yearVO.setYearId(moocYear.getUuid() + "");
            yearVO.setYearName(moocYear.getShowName());
            years.add(yearVO);
        }

        return years;
    }

    @Override
    public FilmDetailVO getFilmDetail(int searchType, String searchParam) {
        FilmDetailVO filmDetailVO;
        //searchType 1-按名称 ，2-按 Id 查找
        if (searchType == 1) {
            filmDetailVO = moocFilmTMapper.getFilmDetailByName("%"+searchParam+"%");
        } else {
            filmDetailVO = moocFilmTMapper.getFilmDetailById(searchParam);
        }

        return filmDetailVO;
    }

    @Override
    public FilmDescVO getFilmDesc(String filmId) {
        MoocFilmInfoT filmInfo = getFilmInfoById(filmId);
        FilmDescVO filmDescVO = new FilmDescVO();
        filmDescVO.setFilmId(filmInfo.getFilmId());
        filmDescVO.setBiography(filmInfo.getBiography());
        return filmDescVO;
    }

    @Override
    public ImgVO getImgs(String filmId) {
        ImgVO imgVO = new ImgVO();
        MoocFilmInfoT filmInfo = getFilmInfoById(filmId);
        //图片是五个以逗号为分割的链接 URL
        String filmImgStr = filmInfo.getFilmImgs();
        String[] filmImgs = filmImgStr.split(",");

        imgVO.setMainImg(filmImgs[0]);
        imgVO.setImg01(filmImgs[1]);
        imgVO.setImg02(filmImgs[2]);
        imgVO.setImg03(filmImgs[3]);
        imgVO.setImg04(filmImgs[4]);
        return imgVO;
    }

    @Override
    public ActorVO getDectInfo(String filmId) {
        ActorVO actorVO = new ActorVO();
        MoocFilmInfoT filmInfo = getFilmInfoById(filmId);
        //获取导演 id
        Integer directorId = filmInfo.getDirectorId();
        MoocActorT moocActorT = moocActorTMapper.selectById(directorId);
        actorVO.setImgAddress(moocActorT.getActorImg());
        actorVO.setDirectorName(moocActorT.getActorName());
        return actorVO;
    }

    @Override
    public List<ActorVO> getActors(String filmIs) {
        List<ActorVO> actors = moocActorTMapper.getActorsByFilmId(filmIs);
        return actors;
    }

    private List<FilmInfo> getFilmInfos(List<MoocFilmT> moocFilms) {
        List<FilmInfo> filmInfos = new ArrayList<>();
        for (MoocFilmT moocFilm : moocFilms) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setScore(moocFilm.getFilmScore());
            filmInfo.setImgAddress(moocFilm.getImgAddress());
            filmInfo.setFilmType(moocFilm.getFilmType());
            filmInfo.setFilmScore(moocFilm.getFilmScore());
            filmInfo.setFilmName(moocFilm.getFilmName());
            filmInfo.setFilmId(moocFilm.getUuid() + "");
            filmInfo.setExpectNum(moocFilm.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilm.getFilmBoxOffice());
            filmInfo.setShowTime(DateUtil.getDay(moocFilm.getFilmTime()));
            filmInfos.add(filmInfo);
        }
        return filmInfos;
    }

    private MoocFilmInfoT getFilmInfoById(String filmId) {
        MoocFilmInfoT moocFilmInfoT = new MoocFilmInfoT();
        moocFilmInfoT.setFilmId(filmId);
        return moocFilmInfoTMapper.selectOne(moocFilmInfoT);
    }
}
