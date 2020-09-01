package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.api.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.api.order.api.OrderServiceAPI;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/18 9:42 下午
 * @Description
 * @Verion 1.0
 */
@RestController
@RequestMapping("/cinema")
@Slf4j
public class CinemaController {
    //设置 cache=true 开启结果缓存
    //connections 设置最大连接数
    @Reference(interfaceClass = CinemaServiceAPI.class, cache = "lru", connections = 100)
    private CinemaServiceAPI cinemaServiceAPI;

    @Reference(interfaceClass = OrderServiceAPI.class)
    private OrderServiceAPI orderServiceAPI;


    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @RequestMapping(value = "/getCinemas")
    public ResponseVO getCinemas(CinemaQueryVO cinemaQueryVO) {
        try {
            // 按照五个条件进行筛选
            Page<CinemaVO> cinemas = cinemaServiceAPI.getCinemas(cinemaQueryVO);
            // 判断是否有满足条件的影院
            if (cinemas.getRecords() == null || cinemas.getRecords().size() == 0) {
                return ResponseVO.success("没有影院可查");
            } else {
                return ResponseVO.success(cinemas.getCurrent(), (int) cinemas.getPages(), "", cinemas.getRecords());
            }

        } catch (Exception e) {
            // 如果出现异常，应该如何处理
            log.error("获取影院列表异常", e);
            return ResponseVO.serviceFail("查询影院列表失败");
        }
    }

    // 获取影院的查询条件
    /*
        1、热点数据 -> 放缓存
        2、banner
     */
    @RequestMapping(value = "/getCondition")
    public ResponseVO<CinemaConditionResponseVO> getCondition(CinemaQueryVO cinemaQueryVO) {
        try {
            // 获取三个集合，然后封装成一个对象返回即可
            List<BrandVO> brands = cinemaServiceAPI.getBrands(cinemaQueryVO.getBrandId());
            List<AreaVO> areas = cinemaServiceAPI.getAreas(cinemaQueryVO.getDistrictId());
            List<HallTypeVO> hallTypes = cinemaServiceAPI.getHallTypes(cinemaQueryVO.getHallType());

            CinemaConditionResponseVO cinemaConditionResponseVO = new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setAreaList(areas);
            cinemaConditionResponseVO.setBrandList(brands);
            cinemaConditionResponseVO.setHalltypeList(hallTypes);

            return ResponseVO.success(cinemaConditionResponseVO);
        } catch (Exception e) {
            log.error("获取条件列表失败", e);
            return ResponseVO.serviceFail("获取影院查询条件失败");
        }
    }


    @RequestMapping(value = "/getFields")
    public ResponseVO<CinemaFieldsResponseVO> getFields(Integer cinemaId) {
        try {

            CinemaInfoVO cinemaInfoById = cinemaServiceAPI.getCinemaInfoById(cinemaId);

            List<FilmInfoVO> filmInfoByCinemaId = cinemaServiceAPI.getFilmInfoByCinemaId(cinemaId);

            CinemaFieldsResponseVO cinemaFieldResponseVO = new CinemaFieldsResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmList(filmInfoByCinemaId);

            return ResponseVO.success(IMG_PRE, cinemaFieldResponseVO);
        } catch (Exception e) {
            log.error("获取播放场次失败", e);
            return ResponseVO.serviceFail("获取播放场次失败");
        }
    }

    @RequestMapping(value = "/getFieldInfo", method = RequestMethod.POST)
    public ResponseVO<CinemaFieldResponseVO> getFieldInfo(Integer cinemaId, Integer fieldId) {
        try {

            CinemaInfoVO cinemaInfoById = cinemaServiceAPI.getCinemaInfoById(cinemaId);
            FilmInfoVO filmInfoByFieldId = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
            HallInfoVO filmFieldInfo = cinemaServiceAPI.getFilmFieldInfo(fieldId);
            filmFieldInfo.setSoldSeats(orderServiceAPI.getSoldSeatsByFieldId(fieldId));

            CinemaFieldResponseVO cinemaFieldResponseVO = new CinemaFieldResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmInfo(filmInfoByFieldId);
            cinemaFieldResponseVO.setHallInfo(filmFieldInfo);

            return ResponseVO.success(IMG_PRE, cinemaFieldResponseVO);
        } catch (Exception e) {
            log.error("获取选座信息失败", e);
            return ResponseVO.serviceFail("获取选座信息失败");
        }
    }
}
