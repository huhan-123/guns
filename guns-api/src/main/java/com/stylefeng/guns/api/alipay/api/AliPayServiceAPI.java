package com.stylefeng.guns.api.alipay.api;

import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;

/**
 * @Author: huhan
 * @Date 2020/8/22 7:47 上午
 * @Description
 * @Verion 1.0
 */
public interface AliPayServiceAPI {
    AliPayInfoVO getQRCode(String orderId);

    AliPayResultVO getOrderStatus(String orderId);
}
