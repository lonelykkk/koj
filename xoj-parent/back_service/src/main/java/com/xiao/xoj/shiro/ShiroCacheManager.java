package com.xiao.xoj.shiro;

import com.xiao.xoj.utils.RedisUtils;
import lombok.Data;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * @author 肖恩
 * @create 2023/6/8 21:04
 * @description: 自定义redis缓存
 */
@Data
public class ShiroCacheManager implements CacheManager {

    private long cacheLive;        // cache存活时间，单位：s秒

    private String cacheKeyPrefix; // cache前缀

    private RedisUtils redisUtils; // redis工具类

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new ShiroCache<K, V>(cacheLive, cacheKeyPrefix, redisUtils);
    }
}
