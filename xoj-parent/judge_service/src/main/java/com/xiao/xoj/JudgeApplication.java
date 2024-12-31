package com.xiao.xoj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author 肖恩
 * @create 2023/3/19 16:26
 * @description: TODO
 */
@SpringBootApplication
@EnableDiscoveryClient  // 开启注册发现
@MapperScan("com.xiao.xoj.mapper")
public class JudgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JudgeApplication.class, args);
    }

}
