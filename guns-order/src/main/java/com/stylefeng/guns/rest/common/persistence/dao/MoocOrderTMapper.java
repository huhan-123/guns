package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author jiangzh
 * @since 2018-09-20
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {

    //获取 json 文件中所有的座位
    String getSeatsByFieldId(@Param("fieldId") String fieldId);

    //根据订单 id 获取订单
    OrderVO getOrderInfoById(@Param("orderId") String orderId);

    //获取当前登录用户所有订单
    List<OrderVO> getOrdersByUserId(@Param("userId")Integer userId, Page<OrderVO> page);

    //获取场次已经出售的座位
    String getSoldSeatsByFieldId(@Param("fieldId")Integer fieldId);

}
