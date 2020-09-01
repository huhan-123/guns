package com.stylefeng.guns.rest.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

/**
 * @Author: huhan
 * @Date 2020/8/20 9:10 下午
 * @Description 限流器
 * @Verion 1.0
 */
@Slf4j
@Data
public class RateLimiter {
    //向桶中添加令牌的速率（表示每秒向桶中添加多少个令牌）
    private final int rate;

    //线程获取令牌超时时间（获取令牌超时直接返回 false）
    private final long timeout;

    //桶的容量
    private final int capacity;

    //当前桶中令牌的个数
    private volatile int token;

    private static final int DEFAULT_RATE = 10;
    private static final long DEFAULT_TIMEOUT = 1;
    private static final int DEFAULT_CAPACITY = 100;

    private static final Unsafe unsafe;
    private static final long tokenOffset;
    private final Object lock;

    //只能通过反射获取 Unsafe 对象
    static {
        try {
            Class<?> clazz = Unsafe.class;
            Field field = clazz.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(clazz);
            tokenOffset = unsafe.objectFieldOffset(RateLimiter.class.getDeclaredField("token"));
        } catch (Exception e) {
            log.error("获取 Unsafe 失败");
            throw new RuntimeException(e);
        }
    }

    public RateLimiter(int rate, long timeout, int capacity) {
        this.rate = rate;
        this.timeout = timeout;
        this.capacity = capacity;
        this.token = capacity;
        lock = new Object();
    }

    public RateLimiter() {
        this(DEFAULT_RATE,DEFAULT_TIMEOUT,DEFAULT_CAPACITY);
    }

    private boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, tokenOffset, expect, update);
    }

    //获取一个令牌
    public boolean acquire() {
        int current = token;
        //剩余等待时间,若小于 0，证明等待超时
        long residual = timeout;
        long futrue = residual + System.currentTimeMillis();

        for (; ; ) {
            //尝试获取令牌，如果成功，返回 true
            if (current > 0 && compareAndSet(current, current - 1)) {
                return true;
            }

            //如果 current>0，重新尝试获取令牌，否则阻塞
            if (current <= 0 && residual > 0) {
                synchronized (lock) {
                    try {
                        lock.wait(residual);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

            }

            residual = futrue - System.currentTimeMillis();
            current = token;
            if (residual <= 0) {
                break;
            }
        }
        return false;
    }

    //向桶中添加令牌，因为只会有一个线程为当前桶添加令牌，因此不用考虑多个线程访问该方法的情况（但是要考虑其他线程修改 token 的情况）
    public void addToken(ExecutorService executorService) {
        //如果令牌桶已满，直接返回，不用再添加令牌
        if (token == capacity) {
            return;
        }

        executorService.execute(() -> {
            int oldToken;
            int newToken;
            //由于其他线程可能修改 token 的值，因此要用死循环加 CAS 的方式修改 token
            for (; ; ) {
                oldToken = token;
                newToken = Math.max(capacity, token + rate);
                if (compareAndSet(oldToken, newToken)) {
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return;
                }
            }
        });
    }

}
