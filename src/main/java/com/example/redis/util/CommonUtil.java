package com.example.redis.util;

/**
 * @Auther: mingweilin
 * @Date: 1/17/2020 16:12
 * @Description:
 */
public class CommonUtil {
    private CommonUtil() {

    }

    public static int getTimestamps() {
        return (int) Math.ceil((double) System.currentTimeMillis() / 1000L);
    }
}
