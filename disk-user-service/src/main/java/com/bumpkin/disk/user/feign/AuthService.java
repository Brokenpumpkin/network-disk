package com.bumpkin.disk.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/05/12 10:39
 */
@Component
@FeignClient(name="cloud-auth")
public interface AuthService {
    @RequestMapping(method = RequestMethod.POST, value = "/oauth/token", headers = {"Content-Type: multipart/form-data", "Authorization=Basic Y2xpZW50XzI6MTIzNDU2"})
    Object postAccessToken(@RequestBody MultiValueMap<String, String> map);

}
