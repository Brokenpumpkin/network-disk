package com.bumpkin.disk.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 19:55
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringCloudApplication
@MapperScan(basePackages = {"com.bumpkin.disk.user.dao"})
@ComponentScan(basePackages = {"com.bumpkin.disk.utils"})
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
