package com.bumpkin.disk.file.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author linnzhiquan
 */
@Slf4j
@Component
public class WebUtil {

    @Autowired
    private static RedisUtil redisUtil;

    public static String getUserNameByRequest(HttpServletRequest request) {
        DiskUser diskUser = (DiskUser) request.getSession().getAttribute("user");
        String userName = "null";
        if(diskUser == null || diskUser.getUsername() == null) {
            return userName;
        }
        return diskUser.getUsername();
    }
    public  DiskUser getUserByRequest(HttpServletRequest request) {
        //获取用户
        String accessToken = StrUtil.subAfter(request.getHeader("Authorization"), " ", false);
        log.info(accessToken);
        String s = redisUtil.get(accessToken);
        return JSONUtil.toBean(JSONUtil.parseObj(s), DiskUser.class);
    }
}
