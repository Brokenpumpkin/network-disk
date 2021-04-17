package com.bumpkin.disk.auth;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

import java.net.InetAddress;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/11 14:11
 */
@Slf4j
@EnableAuthorizationServer
@EnableFeignClients
@EnableDiscoveryClient
//@SpringCloudApplication
@SpringBootApplication
@MapperScan(basePackages={"com.bumpkin.disk.auth.dao"})
public class DiskAuthApplication {
    public static void main(String[] args) throws Exception  {
        SpringApplication.run(DiskAuthApplication.class, args);
//        SpringApplication app = new SpringApplication(DiskAuthApplication.class);
//        Environment env = app.run(args).getEnvironment();
//        String protocol = "http";
//        log.info("\n----------------------------------------------------------\n\t" +
//                        "Application '{}' is running! Access URLs:\n\t" +
//                        "Local: \t\t{}://localhost:{}/doc.html\n\t" +
//                        "External: \t{}://{}:{}/doc.html\n\t" +
//                        "Profile(s): \t{}\n----------------------------------------------------------",
//                env.getProperty("spring.application.name"),
//                protocol,
//                env.getProperty("server.port"),
//                protocol,
//                InetAddress.getLocalHost().getHostAddress(),
//                env.getProperty("server.port"),
//                env.getActiveProfiles());
    }

}
