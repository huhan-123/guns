package com.stylefeng.guns.api.alipay.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/22 7:49 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class AliPayResultVO implements Serializable {
    private String orderId;
    private Integer orderStatus;
    private String orderMsg;
}
