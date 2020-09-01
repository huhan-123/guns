package com.stylefeng.guns.rest.modular.vo;

import lombok.Data;

/**
 * @Author: huhan
 * @Date 2020/8/16 9:23 下午
 * @Description
 * @Verion 1.0
 */
@Data
public class ResponseVO<T> {
    //返回状态【0-成功，1-业务失败，999-系统异常】
    private int status;
    //返回信息
    private String msg;
    //返回数据实体
    private T data;

    //图片前缀
    private String imgPre;

    //当前页
    private int nowPage;

    //总页数
    private int totalPage;

    private ResponseVO() {
    }

    public static<T> ResponseVO<T> success(T data){
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(0);
        responseVO.setData(data);
        return responseVO;
    }

    public static<T> ResponseVO<T> success(String msg){
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(0);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static <T> ResponseVO<T> success(String imgPre, T data) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(0);
        responseVO.setImgPre(imgPre);
        responseVO.setData(data);
        return responseVO;
    }

    public static <T> ResponseVO<T> success(int nowPage,int totalPage,String imgPre, T data) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(0);
        responseVO.setImgPre(imgPre);
        responseVO.setData(data);
        responseVO.setNowPage(nowPage);
        responseVO.setTotalPage(totalPage);
        return responseVO;
    }

    public static <T> ResponseVO<T> serviceFail(String msg) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(1);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static <T> ResponseVO<T> appFail(String msg) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(999);
        responseVO.setMsg(msg);
        return responseVO;
    }
}
