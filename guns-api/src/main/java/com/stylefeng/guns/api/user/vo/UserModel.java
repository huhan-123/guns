package com.stylefeng.guns.api.user.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huhan
 * @Date 2020/8/14 3:45 下午
 * @Description 存储用户注册时的信息
 * @Verion 1.0
 */
@Data
public class UserModel implements Serializable {
    private String username;
    private String password;
    private String email;
    private String address;
    private String phone;
}
