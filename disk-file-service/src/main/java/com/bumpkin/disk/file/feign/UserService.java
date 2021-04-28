package com.bumpkin.disk.file.feign;

import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/28 20:48
 */
@Component
@FeignClient(name="cloud-user-service")
public interface UserService {

    @GetMapping(value = "/user/getUserById")
    DiskUser getUserById(@RequestParam String id);
}
