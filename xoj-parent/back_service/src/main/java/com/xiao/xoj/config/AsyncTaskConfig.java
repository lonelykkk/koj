package com.xiao.xoj.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 肖恩
 * @create 2023/8/12 8:43
 * @description: 通用异步线程池
 */
@Slf4j(topic = "xoj")
@Configuration
public class AsyncTaskConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(8);
        // 最大线程数
        executor.setMaxPoolSize(16);
        // 线程存活时间(秒)
        executor.setKeepAliveSeconds(5);
        // 工作队列长度
        executor.setQueueCapacity(100);
        // 拒绝策略
        // CallerRunsPolicy()：交由调用方线程运行，比如 main 线程
        // AbortPolicy()：直接抛出异常。
        // DiscardPolicy()：直接丢弃。
        // DiscardOldestPolicy()：丢弃队列中最老的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程前缀名
        executor.setThreadNamePrefix("order-send-thread-");
        // 初始化
        executor.initialize();
        return executor;
    }


    /**
     * @des: 异步任务中异常处理
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
                log.error("==========================" + throwable.getMessage() + "=======================", throwable);
                log.error("exception method:" + method.getName());
            }
        };
    }

}
