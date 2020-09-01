package com.stylefeng.guns.api.user.api;

import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;

/**
 * @Author: huhan
 * @Date 2020/8/14 10:42 上午
 * @Description
 * @Verion 1.0
 */
public interface UserAPI {
    int login(String username, String password);

    boolean register(UserModel userModel);

    boolean checkUserName(String username);

    UserInfoModel getUserInfo(int uuid);

    UserInfoModel updateUserInfo(UserInfoModel userInfoModel);
}
