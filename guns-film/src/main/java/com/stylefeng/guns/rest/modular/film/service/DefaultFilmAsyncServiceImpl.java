package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.api.film.api.FilmAsyncServiceAPI;
import com.stylefeng.guns.api.film.vo.ActorVO;
import com.stylefeng.guns.api.film.vo.FilmDescVO;
import com.stylefeng.guns.api.film.vo.ImgVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocActorTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFilmInfoTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocActorT;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmInfoT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/18 4:12 下午
 * @Description
 * @Verion 1.0
 */
@Component
@Service
public class DefaultFilmAsyncServiceImpl implements FilmAsyncServiceAPI {
    @Autowired
    private MoocFilmInfoTMapper moocFilmInfoTMapper;

    @Autowired
    private MoocActorTMapper moocActorTMapper;

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

    private MoocFilmInfoT getFilmInfoById(String filmId) {
        MoocFilmInfoT moocFilmInfoT = new MoocFilmInfoT();
        moocFilmInfoT.setFilmId(filmId);
        return moocFilmInfoTMapper.selectOne(moocFilmInfoT);
    }
}
