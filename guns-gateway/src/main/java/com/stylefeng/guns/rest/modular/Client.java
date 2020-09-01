package com.stylefeng.guns.rest.modular;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.api.UserAPI;
import org.springframework.stereotype.Component;

/**
 * @Author: huhan
 * @Date 2020/8/14 3:02 下午
 * @Description
 * @Verion 1.0
 */
@Component
public class Client {
    @Reference(interfaceClass = UserAPI.class)
    public UserAPI userAPI;

    public void run() {
        userAPI.login("huhan", "fakjsdk");
    }
}
