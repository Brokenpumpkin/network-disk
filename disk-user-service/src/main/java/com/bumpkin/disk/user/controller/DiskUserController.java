package com.bumpkin.disk.user.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.user.dto.DiskUserLoginDto;
import com.bumpkin.disk.user.dto.DiskUserRegisterDto;
import com.bumpkin.disk.user.service.DiskUserService;
import com.bumpkin.disk.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

import javax.validation.Valid;
import java.util.Collections;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 20:10
 */
@Api(tags = "网盘用户服务")
@RestController
@RequestMapping(value = "/user")
public class DiskUserController {

    @Value("${security.oauth2.resource.token-info-uri}")
    private String tokenUrl;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DiskUserService diskUserService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "登录")
    @PostMapping(value = "/login")
    public ResponseResult login(@RequestBody @Valid DiskUserLoginDto userLoginDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseResult.createErrorResult(result.getFieldError().getDefaultMessage());
        }
        String username = userLoginDto.getUsername();
        String phoneNum = userLoginDto.getPhoneNum();
        String password = userLoginDto.getPassword();
        DiskUser diskUser = diskUserService.getUserByUsername(username);
        if (diskUser == null) {
            return ResponseResult.createErrorResult("用户不存在！");
        }
        if (!passwordEncoder.matches(password, diskUser.getPassword())) {
            return ResponseResult.createErrorResult("密码错误！");
        }
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.put("grant_type", Collections.singletonList("password"));
        parameters.put("username", Collections.singletonList(username));
        parameters.put("password", Collections.singletonList(password));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(clientId, clientSecret));
        OAuth2AccessToken token = restTemplate.postForObject(tokenUrl +"/oauth/token",parameters,OAuth2AccessToken.class);
        assert token != null;
        redisUtil.set(token.getValue(), JSONUtil.toJsonStr(diskUser));
        return ResponseResult.createSuccessResult(token, "登录成功！");
    }

    @ApiOperation(value = "注册")
    @PostMapping(value = "/register")
    public ResponseResult register(@RequestBody @Valid DiskUserRegisterDto userRegisterDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseResult.createErrorResult(result.getFieldError().getDefaultMessage());
        }
        return diskUserService.add(userRegisterDto);
    }
}