package com.stylefeng.guns.api.film.vo;

import com.stylefeng.guns.api.film.vo.ActorVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: huhan
 * @Date 2020/8/18 3:28 下午
 * @Description
 * @Verion 1.0
 */
@Data
public class ActorRequestVO implements Serializable {
    private ActorVO director;
    private List<ActorVO> actors;
}
