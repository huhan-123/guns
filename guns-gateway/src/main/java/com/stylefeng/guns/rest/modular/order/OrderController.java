package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.api.AliPayServiceAPI;
import com.stylefeng.guns.api.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVO;
import com.stylefeng.guns.api.cinema.api.CinemaServiceAPI;
import com.stylefeng.guns.api.order.api.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.MoocOrderT;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.config.RateLimiter;
import com.stylefeng.guns.rest.config.RateLimiterManager;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/order")
public class OrderController {
    @Reference(interfaceClass = OrderServiceAPI.class,filter = {"tracing"})
    private OrderServiceAPI orderServiceAPI;

    @Reference(interfaceClass = AliPayServiceAPI.class,filter = {"tracing"})
    private AliPayServiceAPI aliPayServiceAPI;

    @Reference(interfaceClass = CinemaServiceAPI.class, filter = {"tracing"})
    private CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    private RateLimiterManager rateLimiterManager;

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @HystrixCommand(fallbackMethod = "error", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
            //如果调用超过指定时间没有返回，执行降级逻辑
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            //服务错误百分比超过了阈值，熔断器开关自动打开，一段时间内停止对该服务的所有请求。
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "1"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
            })
    // 购票
    //注意，Hystrix 针对的是当前服务（这里就是 /buyTickets 接口），而不是其调用的服务，如果当前服务不可用或异常，就会降级熔断
    @RequestMapping(value = "/buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId, String soldSeats, String seatsName) {
        if (fieldId == null || soldSeats == null || soldSeats.isEmpty()) {
            return ResponseVO.serviceFail("参数不合法");
        }
        try {
            RateLimiter rateLimiter = rateLimiterManager.getLimiter("/order/buyTickets", 10, 1, 100);
            if (!rateLimiter.acquire()) {
                return ResponseVO.serviceFail("购票人数过多，请稍后再试");
            }

            // 验证售出的票是否为真
            boolean isTrue = orderServiceAPI.isTrueSeats(fieldId + "", soldSeats);

            // 已经销售的座位里，有没有这些座位
            boolean hasSold =cinemaServiceAPI.hasSold(fieldId, soldSeats);

            // 验证，上述两个内容有一个不为真，则不创建订单信息
            if (isTrue && !hasSold) {
                // 创建订单信息,注意获取登陆人
                String userId = CurrentUser.getUserId();
                if (userId == null || userId.trim().length() == 0) {
                    return ResponseVO.serviceFail("用户未登陆");
                }
                //先插入草稿状态订单
                MoocOrderT moocOrderT = orderServiceAPI.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
                //使用分布式事务保证数据一致性（修改已售座位和插入订单要同时成功或同时失败）
                boolean result = orderServiceAPI.makePayment(moocOrderT, soldSeats);

                if (!result) {
                    log.error("购票未成功");
                    return ResponseVO.serviceFail("购票业务异常");
                } else {
                    return ResponseVO.success(moocOrderT);
                }
            } else {
                return ResponseVO.serviceFail("订单中的座位编号有问题");
            }
        } catch (Exception e) {
            log.error("购票业务异常：", e);
            return ResponseVO.serviceFail("购票业务异常");
        }
    }

    //降级逻辑
    public ResponseVO error(Integer fieldId,String soldSeats,String seatsName){
        return ResponseVO.serviceFail("抱歉，下单的人太多了，请稍后重试");
    }

    @RequestMapping(value = "/getOrderInfo", method = RequestMethod.POST)
    public ResponseVO getOrderInfo(
            @RequestParam(name = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") Integer pageSize
    ) {
        // 获取当前登录人的信息
        String userId = CurrentUser.getUserId();

        // 获取当前登录人订单
        Page<OrderVO> page = new Page<>(nowPage, pageSize);
        if (userId != null && userId.trim().length() > 0) {
            Page<OrderVO> result = orderServiceAPI.getOrderByUserId(Integer.parseInt(userId), page);
            return ResponseVO.success(nowPage, (int) result.getPages(), "", result.getRecords());

        } else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }

    @PostMapping("/getPayInfo")
    public ResponseVO getPayInfo(@RequestParam("orderId") String orderId) {
        AliPayInfoVO aliPayInfoVO = aliPayServiceAPI.getQRCode(orderId);
        return ResponseVO.success(IMG_PRE, aliPayInfoVO);
    }

    @PostMapping("/getPayResult")
    public ResponseVO getPayResult(@RequestParam("orderId") String orderId,
                                   @RequestParam(name = "tryNums",required = false,defaultValue = "1") Integer tryNums) {
        //判断是否超时
        if (tryNums >3) {
            return ResponseVO.serviceFail("订单支付失败，请稍后重试");
        }else {
            AliPayResultVO orderStatus = aliPayServiceAPI.getOrderStatus(orderId);
            if (orderStatus==null|| StringUtils.isEmpty(orderStatus.getOrderId())) {
                orderStatus = new AliPayResultVO();
                orderStatus.setOrderId(orderId);
                orderStatus.setOrderStatus(0);
                orderStatus.setOrderMsg("支付不成功");
            }
            return ResponseVO.success(orderStatus);
        }
    }
}
