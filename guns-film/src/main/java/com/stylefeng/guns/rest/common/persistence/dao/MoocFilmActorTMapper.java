package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.film.vo.ActorVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmActorT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 影片与演员映射表 Mapper 接口
 * </p>
 *
 * @author huhan
 * @since 2020-08-18
 */
public interface MoocFilmActorTMapper extends BaseMapper<MoocFilmActorT> {
}
