package com.xiao.xoj.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 肖恩
 * @create 2023/8/11 15:52
 * @description: AI对话限制工具类
 */
@Slf4j(topic = "AI对话限制")
@Component
public class AIChatLimitUtil {

    @Autowired
    private RedisUtils redisUtils;

    private final Integer aiMaxCount = 20; // 最大同时使用人数为 20人

    public final static String AI_NOW_COUNT = "ai_now_count";

    /**
     * @des: 是否存在令牌，是，返回true；否，返回false
     */
    public synchronized boolean hasToken() {
        Integer nowCount = (Integer) redisUtils.get(AI_NOW_COUNT);
        if (nowCount == null) {
            redisUtils.set(AI_NOW_COUNT, 1);
            return true;
        }
        if (nowCount < aiMaxCount)
            return true;
        return false;
    }


    /**
     * @des： 令牌数量 -1
     */
    public void decrToken() {
        redisUtils.incr(AI_NOW_COUNT, 1);
    }


    /**
     * @des： 令牌数量 +1
     */
    public void incrToken() {
        redisUtils.decr(AI_NOW_COUNT, 1);
    }

}
