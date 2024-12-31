package com.xiao.xoj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * @author 肖恩
 * @create 2023/3/19 15:35
 * @description: TODO
 */
@SpringBootApplication
@MapperScan("com.xiao.xoj.mapper")
@EnableDiscoveryClient  // 开启注册发现
@EnableAsync(proxyTargetClass=true) // 开启异步注解
@EnableFeignClients
public class BackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

}
