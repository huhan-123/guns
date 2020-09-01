package com.stylefeng.guns.rest.modular.film.vo;

import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import lombok.Data;

import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/18 7:49 上午
 * @Description /film/getCondition接口返回对象
 * @Verion 1.0
 */
@Data
public class FilmConditionVO {
    private List<CatVO> catInfo;

    private List<SourceVO> sourceInfo;

    private List<YearVO> yearInfo;
}
