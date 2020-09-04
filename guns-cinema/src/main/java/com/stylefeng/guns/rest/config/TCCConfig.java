package com.stylefeng.guns.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @Author: huhan
 * @Date 2020/9/2 10:11 上午
 * @Description
 * @Verion 1.0
 */
@Configuration
@ImportResource(locations = {"classpath:tcc-transaction.xml","classpath:tcc-transaction-dubbo.xml"})
public class TCCConfig {
}
