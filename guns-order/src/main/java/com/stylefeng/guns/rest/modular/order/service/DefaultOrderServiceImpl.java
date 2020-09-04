package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.api.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.api.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.MoocOrderT;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.mengyun.tcctransaction.api.Compensable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@Service(interfaceClass = OrderServiceAPI.class, filter = {"tracing"}, timeout = 10000)
public class DefaultOrderServiceImpl implements OrderServiceAPI {

    @Autowired
    private MoocOrderTMapper moocOrderTMapper;

    //    @Reference(interfaceClass = CinemaServiceAPI.class, filter = {"tracing"})
    @Autowired
    private CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    private FTPUtil ftpUtil;

    // 验证是否为真实的座位编号
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        // 根据FieldId找到对应的座位位置图
        String seatPath = moocOrderTMapper.getSeatsByFieldId(fieldId);

        // 读取位置图，判断seats是否为真
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);

        // 将fileStrByAddress转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);
        // seats=1,2,3   ids="1,3,4,5,6,7,88"
        String ids = jsonObject.get("ids").toString();

        // 每一次匹配上的，都给isTrue+1
        String[] seatArrs = seats.split(",");
        String[] idArrs = ids.split(",");
        int isTrue = 0;
        for (String id : idArrs) {
            for (String seat : seatArrs) {
                if (seat.equalsIgnoreCase(id)) {
                    isTrue++;
                }
            }
        }

        // 如果匹配上的数量与已售座位数一致，则表示全都匹配上了
        if (seatArrs.length == isTrue) {
            return true;
        } else {
            return false;
        }
    }

    /*// 判断是否为已售座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id", fieldId);

        List<MoocOrderT> list = moocOrderTMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");
        // 有任何一个编号匹配上，则直接返回失败
        for (MoocOrderT moocOrderT : list) {
            String[] ids = moocOrderT.getSeatsIds().split(",");
            for (String id : ids) {
                for (String seat : seatArrs) {
                    if (id.equalsIgnoreCase(seat)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }*/

    // 插入订单（草稿状态）
    @Override
    public MoocOrderT saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {

        // 编号
        String uuid = UUIDUtil.genUuid();

        // 影片信息
        FilmInfoVO filmInfoVO = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.parseInt(filmInfoVO.getFilmId());

        // 获取影院信息
        OrderQueryVO orderQueryVO = cinemaServiceAPI.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        double filmPrice = Double.parseDouble(orderQueryVO.getFilmPrice());

        // 求订单总金额  // 1,2,3,4,5
        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds, filmPrice);

        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(uuid);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderPrice(totalPrice);
        moocOrderT.setFilmPrice(filmPrice);
        moocOrderT.setFilmId(filmId);
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setCinemaId(cinemaId);
        moocOrderT.setOrderStatus(3);
        moocOrderT.setVersion(1);

        Integer insert = moocOrderTMapper.insert(moocOrderT);
        if (insert > 0) {
            return moocOrderT;
        } else {
            // 插入出错
            log.error("订单插入失败");
            return null;
        }
    }

    private static double getTotalPrice(int solds, double filmPrice) {
        BigDecimal soldsDeci = new BigDecimal(solds);
        BigDecimal filmPriceDeci = new BigDecimal(filmPrice);

        BigDecimal result = soldsDeci.multiply(filmPriceDeci);

        // 四舍五入，取小数点后两位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
    }


    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {
        Page<OrderVO> result = new Page<>();
        if (userId == null) {
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        } else {
            List<OrderVO> ordersByUserId = moocOrderTMapper.getOrdersByUserId(userId, page);
            if (ordersByUserId == null && ordersByUserId.size() == 0) {
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            } else {
                // 获取订单总数
                EntityWrapper<MoocOrderT> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("order_user", userId);
                Integer counts = moocOrderTMapper.selectCount(entityWrapper);
                // 将结果放入Page
                result.setTotal(counts);
                result.setRecords(ordersByUserId);

                return result;
            }
        }
    }

    // 根据放映查询，获取所有的已售座位
    /*

        1  1,2,3,4
        1  5,6,7

     */
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if (fieldId == null) {
            log.error("查询已售座位错误，未传入任何场次编号");
            return "";
        } else {
            return moocOrderTMapper.getSoldSeatsByFieldId(fieldId);
        }
    }

    @Override
    public OrderVO getOrderInfoById(String orderId) {
        return moocOrderTMapper.getOrderInfoById(orderId);
    }

    @Override
    public boolean paySuccess(String orderId) {
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(1);

        Integer integer = moocOrderTMapper.updateById(moocOrderT);
        if (integer >= 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean payFail(String orderId) {
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(2);

        Integer integer = moocOrderTMapper.updateById(moocOrderT);
        if (integer >= 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Compensable(confirmMethod = "confirmMakePayment", cancelMethod = "cancelMakePayment", asyncConfirm = true)
    public boolean makePayment(MoocOrderT moocOrderT, String soldSeats) {
        //如果订单是草稿状态，则修改为支付中
        if (moocOrderT.getOrderStatus() == MoocOrderT.DRAFT) {
            moocOrderT.setOrderStatus(MoocOrderT.PAYING);
            moocOrderT.setVersion(moocOrderT.getVersion() + 1);

            EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
            wrapper.eq("UUID", moocOrderT.getUuid())
                    .and()
                    .eq("version", moocOrderT.getVersion() - 1);
            Integer effectRows = moocOrderTMapper.update(moocOrderT, wrapper);

            //如果订单状态修改失败，直接返回false
            if (effectRows <= 0) {
                return false;
            }
        }

        //更新座位
        if (!cinemaServiceAPI.hasSold(moocOrderT.getFieldId(), soldSeats)) {
            cinemaServiceAPI.addSoldSeats(moocOrderT.getFieldId(), soldSeats);
        }
        return true;
    }

    public void confirmMakePayment(MoocOrderT moocOrderT, String soldSeats) {
        MoocOrderT currentMoocOrderT = moocOrderTMapper.selectOrderById(moocOrderT.getUuid());
        //如果状态不为PAYING，证明其它线程已经修改了订单状态，为保证幂等性，直接返回
        if (currentMoocOrderT != null && currentMoocOrderT.getOrderStatus() == MoocOrderT.PAYING) {
            currentMoocOrderT.setOrderStatus(MoocOrderT.WAIT);
            updateOrder(currentMoocOrderT);
        }
    }

    public void cancelMakePayment(MoocOrderT moocOrderT, String soldSeats) {
        MoocOrderT currentMoocOrderT = moocOrderTMapper.selectOrderById(moocOrderT.getUuid());
        //如果状态不为PAYING，证明其它线程已经修改了订单状态，为保证幂等性，直接返回
        if (currentMoocOrderT != null && currentMoocOrderT.getOrderStatus() == MoocOrderT.PAYING) {
            currentMoocOrderT.setOrderStatus(MoocOrderT.CANCEL);
            updateOrder(currentMoocOrderT);
        }
    }

    private void updateOrder(MoocOrderT currentMoocOrderT) {
        currentMoocOrderT.setVersion(currentMoocOrderT.getVersion() + 1);

        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID", currentMoocOrderT.getUuid()).and().eq("version", currentMoocOrderT.getVersion() - 1);
        Integer effectCount = moocOrderTMapper.update(currentMoocOrderT, wrapper);

        //更新失败，抛出异常，事务管理器重试
        if (effectCount <= 0) {
            throw new RuntimeException("confirm or cancel makePayment failed");
        }
    }
}
