package com.stylefeng.guns.api.alipay.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/22 7:48 上午
 * @Description
 * @Verion 1.0
 */
@Data
public class AliPayInfoVO implements Serializable {
    private String orderId;
    private String QRCodeAddress;
}
