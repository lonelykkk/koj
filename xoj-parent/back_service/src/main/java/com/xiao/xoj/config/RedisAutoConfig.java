package com.xiao.xoj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author 肖恩
 * @create 2023/5/21 18:53
 * @description: TODO
 */
@Configuration
public class RedisAutoConfig {

    @Value("${spring.redis.jedis.pool.max-active:200}")
    private Integer maxActive;

    @Value("${spring.redis.jedis.pool.max-idle:50}")
    private Integer maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait:-1}")
    private Long maxWait;

    @Value("${spring.redis.jedis.pool.min-idle:10}")
    private Integer minIdle;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;
//    @Value("${redis-password}")
//    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig,
                                                         RedisStandaloneConfiguration redisStandaloneConfiguration) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
        return jedisConnectionFactory;
    }

    @Bean
    public JedisPoolConfig jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxActive);
        return jedisPoolConfig;
    }

    @Bean
    public RedisStandaloneConfiguration jedisConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        return config;
    }
}
