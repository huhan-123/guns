package com.stylefeng.guns.api.order.api;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;

public interface OrderServiceAPI {

    // 验证售出的票是否为真(有可能传进来的座位在该场次所在的厅没有)
    boolean isTrueSeats(String fieldId,String seats);

    // 已经销售的座位里，有没有这些座位（避免重复销售同一个座位）
    boolean isNotSoldSeats(String fieldId,String seats);

    // 创建订单信息
    OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId);

    // 获取当前登陆人已经购买的订单
    Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page);

    // 根据 FieldId 获取所有已经销售的座位编号
    String getSoldSeatsByFieldId(Integer fieldId);

    //根据订单编号获取订单信息
    OrderVO getOrderInfoById(String orderId);

    boolean paySuccess(String orderId);

    boolean payFail(String orderId);
}
