package com.bumpkin.disk.user.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.user.dto.DiskUserLoginDto;
import com.bumpkin.disk.user.dto.DiskUserRegisterDto;
import com.bumpkin.disk.user.service.DiskUserService;
import com.bumpkin.disk.user.service.VirtualAddressService;
import com.bumpkin.disk.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
//        String phoneNum = userLoginDto.getPhoneNum();
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
        JSONObject tokenJson = restTemplate.postForObject(tokenUrl +"/oauth/token",parameters, JSONObject.class);
        assert tokenJson != null;
        redisUtil.set(tokenJson.getString("access_token"), JSONUtil.toJsonStr(diskUser));
        return ResponseResult.createSuccessResult(tokenJson, "登录成功！");
    }

    @Transactional
    @ApiOperation(value = "注册")
    @PostMapping(value = "/register")
    public ResponseResult register(@RequestBody @Valid DiskUserRegisterDto userRegisterDto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return ResponseResult.createErrorResult(result.getFieldError().getDefaultMessage());
        }
        return diskUserService.add(userRegisterDto);
    }

    @GetMapping(value = "/getUserById")
    public DiskUser getUserById(@RequestParam String id) {
        return diskUserService.getBaseMapper().selectById(id);
    }
}
