package com.bumpkin.disk.user.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 16:22
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean(value = "createRestApi")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        //标题
                        .title("项目API接口")
                        //版本信息
                        .version("1.0")
                        //描述消息
                        .description("接口列表")
                        .contact(new Contact("项目API接口","localhost:"+ serverPort +"/doc.html","xxxx@qq.com"))
                        .license("mamba")
//                        .licenseUrl("http://www.xxxx.com/")
                        .build())
                //最终调用接口后会和paths拼接在一起
                .pathMapping("/")
                .select()
                //包路径
                .apis(RequestHandlerSelectors.basePackage("com.bumpkin.disk.user.controller"))
                //过滤的接口
                .paths(PathSelectors.any())
                .build();
    }
}
