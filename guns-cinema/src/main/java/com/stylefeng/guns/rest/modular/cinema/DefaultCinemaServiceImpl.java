package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.api.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.dubbo.context.DubboTransactionContextEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/19 7:44 上午
 * @Description
 * @Verion 1.0
 */
@Component
@Slf4j
//execute 设置最大线程数
//@Service(interfaceClass = CinemaServiceAPI.class,executes = 100)
@Service(interfaceClass = CinemaServiceAPI.class, filter = {"tracing"})
public class DefaultCinemaServiceImpl implements CinemaServiceAPI {
    @Autowired
    private MoocCinemaTMapper moocCinemaTMapper;
    @Autowired
    private MoocAreaDictTMapper moocAreaDictTMapper;
    @Autowired
    private MoocBrandDictTMapper moocBrandDictTMapper;
    @Autowired
    private MoocHallDictTMapper moocHallDictTMapper;
    @Autowired
    private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;
    @Autowired
    private MoocFieldTMapper moocFieldTMapper;


    //1、根据CinemaQueryVO，查询影院列表
    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {
        // 业务实体集合
        List<CinemaVO> cinemas = new ArrayList<>();

        Page<MoocCinemaT> page = new Page<>(cinemaQueryVO.getNowPage(), cinemaQueryVO.getPageSize());
        // 判断是否传入查询条件 -> brandId,distId,hallType 是否==99
        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if (cinemaQueryVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaQueryVO.getBrandId());
        }
        if (cinemaQueryVO.getDistrictId() != 99) {
            entityWrapper.eq("area_id", cinemaQueryVO.getDistrictId());
        }
        if (cinemaQueryVO.getHallType() != 99) {  // %#3#%
            entityWrapper.like("hall_ids", "%#+" + cinemaQueryVO.getHallType() + "+#%");
        }

        // 将数据实体转换为业务实体
        List<MoocCinemaT> moocCinemaTS = moocCinemaTMapper.selectPage(page, entityWrapper);
        for (MoocCinemaT moocCinemaT : moocCinemaTS) {
            CinemaVO cinemaVO = new CinemaVO();

            cinemaVO.setUuid(moocCinemaT.getUuid() + "");
            cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice() + "");
            cinemaVO.setCinemaName(moocCinemaT.getCinemaName());
            cinemaVO.setAddress(moocCinemaT.getCinemaAddress());

            cinemas.add(cinemaVO);
        }

        // 根据条件，判断影院列表总数
        long counts = moocCinemaTMapper.selectCount(entityWrapper);

        // 组织返回值对象
        Page<CinemaVO> result = new Page<>();
        result.setRecords(cinemas);
        result.setSize(cinemaQueryVO.getPageSize());
        result.setTotal(counts);

        return result;
    }

    //2、根据条件获取品牌列表[除了就99以外，其他的数字为isActive]
    @Override
    public List<BrandVO> getBrands(int brandId) {
        boolean flag = false;
        List<BrandVO> brandVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);
        // 判断brandId 是否等于 99
        if (brandId == 99 || moocBrandDictT == null || moocBrandDictT.getUuid() == null) {
            flag = true;
        }
        // 查询所有列表
        List<MoocBrandDictT> moocBrandDictTS = moocBrandDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for (MoocBrandDictT brand : moocBrandDictTS) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid() + "");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if (flag) {
                if (brand.getUuid() == 99) {
                    brandVO.setActive(true);
                }
            } else {
                if (brand.getUuid() == brandId) {
                    brandVO.setActive(true);
                }
            }

            brandVOS.add(brandVO);
        }

        return brandVOS;
    }

    //3、获取行政区域列表
    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag = false;
        List<AreaVO> areaVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);
        // 判断brandId 是否等于 99
        if (areaId == 99 || moocAreaDictT == null || moocAreaDictT.getUuid() == null) {
            flag = true;
        }
        // 查询所有列表
        List<MoocAreaDictT> moocAreaDictTS = moocAreaDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for (MoocAreaDictT area : moocAreaDictTS) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid() + "");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if (flag) {
                if (area.getUuid() == 99) {
                    areaVO.setActive(true);
                }
            } else {
                if (area.getUuid() == areaId) {
                    areaVO.setActive(true);
                }
            }

            areaVOS.add(areaVO);
        }

        return areaVOS;
    }

    //4、获取影厅类型列表
    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag = false;
        List<HallTypeVO> hallTypeVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallType);
        // 判断brandId 是否等于 99
        if (hallType == 99 || moocHallDictT == null || moocHallDictT.getUuid() == null) {
            flag = true;
        }
        // 查询所有列表
        List<MoocHallDictT> moocHallDictTS = moocHallDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for (MoocHallDictT hall : moocHallDictTS) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHalltypeName(hall.getShowName());
            hallTypeVO.setHalltypeId(hall.getUuid() + "");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if (flag) {
                if (hall.getUuid() == 99) {
                    hallTypeVO.setActive(true);
                }
            } else {
                if (hall.getUuid() == hallType) {
                    hallTypeVO.setActive(true);
                }
            }

            hallTypeVOS.add(hallTypeVO);
        }

        return hallTypeVOS;
    }

    //5、根据影院编号，获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {

        MoocCinemaT moocCinemaT = moocCinemaTMapper.selectById(cinemaId);
        if (moocCinemaT == null) {
            return new CinemaInfoVO();
        }
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(moocCinemaT.getUuid() + "");
        cinemaInfoVO.setCinemaId(moocCinemaT.getCinemaAddress());

        return cinemaInfoVO;
    }

    //6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {

        return moocFieldTMapper.getFilmInfos(cinemaId);
    }

    //7、根据放映场次ID获取放映信息
    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {

        return moocFieldTMapper.getHallInfo(fieldId);
    }

    //8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {

        return moocFieldTMapper.getFilmInfoById(fieldId);
    }

    @Override
    public OrderQueryVO getOrderNeeds(int fieldId) {

        OrderQueryVO orderQueryVO = new OrderQueryVO();

        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);

        orderQueryVO.setCinemaId(moocFieldT.getCinemaId() + "");
        orderQueryVO.setFilmPrice(moocFieldT.getPrice() + "");

        return orderQueryVO;
    }

    @Override
    public boolean hasSold(int fieldId, String seats) {
        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);
        String soldSeats = moocFieldT.getSoldSeats();
        if (soldSeats != null && !soldSeats.isEmpty()) {
            String[] soldSeatArray = soldSeats.split(",");
            for (String seat : seats.split(",")) {
                for (String soldSeat : soldSeatArray) {
                    if (seat.equals(soldSeat)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    @Compensable(confirmMethod = "confimAddSoldSeats", cancelMethod = "cancelAddSoldSeats", transactionContextEditor = DubboTransactionContextEditor.class)
    public void addSoldSeats(Integer fieldId, String seats) {
        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);

        //如果座位已经售出，抛出异常
        String soldSeats = moocFieldT.getSoldSeats();
        if (soldSeats != null && !soldSeats.isEmpty()) {
            String[] soldSeatArray = soldSeats.split(",");
            for (String seat : seats.split(",")) {
                for (String soldSeat : soldSeatArray) {
                    if (seat.equals(soldSeat)) {
                        throw new RuntimeException("座位已经售出");
                    }
                }
            }
        }

        //座位没有售出，更新座位
        moocFieldT.setSoldSeats(moocFieldT.getSoldSeats() + "," + seats);
        moocFieldT.setVersion(moocFieldT.getVersion() + 1);
        EntityWrapper<MoocFieldT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", moocFieldT.getUuid())
                .and()
                .eq("version", moocFieldT.getVersion() - 1);
        Integer effectRows = moocFieldTMapper.update(moocFieldT, wrapper);
        if (effectRows <= 0) {
            throw new RuntimeException("更新座位失败");
        }
    }

    public void confimAddSoldSeats(Integer fieldId, String seats) {
    }

    public void cancelAddSoldSeats(Integer fieldId, String seats) {
        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);

        //删除try阶段已经售出的座位
        String soldSeats = moocFieldT.getSoldSeats();
        StringBuilder newSoldSeats = new StringBuilder();
        if (soldSeats != null && !soldSeats.isEmpty()) {
            String[] soldSeatArray = soldSeats.split(",");
            for (String soldSeat : soldSeatArray) {
                boolean flag = false;
                for (String seat : seats.split(",")) {
                    if (soldSeat.equals(seat)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    newSoldSeats.append(",").append(soldSeat);
                }
            }
        }
        newSoldSeats.deleteCharAt(0);
        moocFieldT.setSoldSeats(newSoldSeats.toString());
        moocFieldT.setVersion(moocFieldT.getVersion() + 1);

        EntityWrapper<MoocFieldT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", fieldId)
                .and()
                .eq("version", moocFieldT.getVersion() - 1);
        Integer effectRows = moocFieldTMapper.update(moocFieldT, wrapper);

        //更新失败，抛出异常
        if (effectRows <= 0) {
            throw new RuntimeException("cancelAddSoldSeats failed");
        }
    }
}
