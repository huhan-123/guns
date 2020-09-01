package com.stylefeng.guns.rest.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: huhan
 * @Date 2020/8/21 7:29 上午
 * @Description 限流管理器
 * @Verion 1.0
 */
@Component
public class RateLimiterManager {
    //定时线程，用于定时向令牌桶中加入令牌(只是用于启动任务，真正执行添加操作的是其他线程)
    private final ScheduledThreadPoolExecutor scheduledSupplier = new ScheduledThreadPoolExecutor(2);

    //真正执行令牌添加任务的线程
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
            100,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

    //限流管理容器
    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    //相当于 xml 配置文件中的 <init-method> 标签
    @PostConstruct
    public void init() {
        scheduledSupplier.scheduleAtFixedRate(new RateLimiterSupplier(), 1, 1, TimeUnit.SECONDS);
    }

    //相当于 xml 配置文件中的<destory-method>标签
    @PreDestroy
    public void destory() {
        scheduledSupplier.shutdown();
        executor.shutdown();
    }

    public RateLimiter getLimiter(String key, int rate, int timeout, int capacity) {
        RateLimiter rateLimiter = rateLimiterMap.get(key);
        if (rateLimiter == null) {
            synchronized (this) {
                rateLimiter = new RateLimiter(rate, timeout, capacity);
                rateLimiterMap.put(key, rateLimiter);
            }
        }
        return rateLimiter;
    }

    private class RateLimiterSupplier implements Runnable {
        @Override
        public void run() {
            rateLimiterMap.values().forEach(rateLimiter -> rateLimiter.addToken(executor));
        }
    }

}
