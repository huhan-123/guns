package com.stylefeng.guns.rest.common;

/**
 * @Author: huhan
 * @Date 2020/8/16 9:41 下午
 * @Description
 * @Verion 1.0
 */
public class CurrentUser {
    //InheritableThreadLocal可以获取父线程中的值
    private static final InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    public static void saveUserId(String userId) {
        threadLocal.set(userId);
    }

    public static String getUserId() {
        return threadLocal.get();
    }
}
