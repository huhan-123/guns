package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import sun.net.ftp.FtpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: huhan
 * @Date 2020/8/20 7:14 上午
 * @Description
 * @Verion 1.0
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ftp")
@Data
public class FTPUtil {
    private String hostName;

    private Integer port = 21;

    private String userName;

    private String password;

    FTPClient ftpClient = null;

    public void init() {
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName, port);
            ftpClient.login(userName, password);
        } catch (Exception e) {
            log.error("初始化失败：" + e);
        }
    }

    //输入一个路径，将路径里的文件转化为字符串返回
    public String getFileStrByAddress(String path) {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = null;
        try {
            init();
            InputStream inputStream = ftpClient.retrieveFileStream(path);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while ((str = reader.readLine()) != null) {
                result.append(str.trim());
            }
            ftpClient.logout();
        } catch (Exception e) {
            log.error("获取文件失败：" + e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        FTPUtil ftpUtil = new FTPUtil();
        ftpUtil.init();
        String str = ftpUtil.getFileStrByAddress("/seats/cgs.json");
        System.out.println(str);
    }

}
