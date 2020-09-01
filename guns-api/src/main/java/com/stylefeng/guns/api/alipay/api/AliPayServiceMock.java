package com.stylefeng.guns.api.alipay.api;

import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;

/**
 * @Author: huhan
 * @Date 2020/8/23 3:23 下午
 * @Description 当出现 RpcException 时，会执行该方法中的逻辑进行降级，需要在@service 中指定mock=该类的全限定名
 * @Verion 1.0
 */
public class AliPayServiceMock implements AliPayServiceAPI{
    @Override
    public AliPayInfoVO getQRCode(String orderId) {
        return null;
    }

    @Override
    public AliPayResultVO getOrderStatus(String orderId) {
        AliPayResultVO aliPayResultVO = new AliPayResultVO();
        aliPayResultVO.setOrderId(orderId);
        aliPayResultVO.setOrderStatus(0);
        aliPayResultVO.setOrderMsg("尚未支付成功");
        return aliPayResultVO;
    }
}
