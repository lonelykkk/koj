package com.xiao.xoj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 肖恩
 * @create 2023/8/12 9:23
 * @description: 专用于判题的异步线程池
 */
@Configuration
@EnableAsync
public class JudgeAsyncTaskConfig {

    @Bean
    public Executor judgeTaskAsyncPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8); // 核心线程数
        executor.setMaxPoolSize(16); // 最大线程数
        executor.setQueueCapacity(150); // 工作队列容量
        executor.setKeepAliveSeconds(5); // 线程存活时间
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略，由线程调用者处理被拒绝的任务
        executor.setThreadNamePrefix("judgeExecutor-thread-"); // 线程前缀名
        executor.initialize();
        return executor;
    }

}
