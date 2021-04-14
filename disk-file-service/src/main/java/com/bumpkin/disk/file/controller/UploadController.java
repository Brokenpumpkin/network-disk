package com.bumpkin.disk.file.controller;

import cn.hutool.json.JSONUtil;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.utils.RedisUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 20:13
 */
@Api(tags = "上传文件")
@RequestMapping("/upload")
@RestController
public class UploadController {

    @Autowired
    public DiskFileService diskFileService;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping(value = "/fileUpload")
    public ResponseResult upload(@RequestParam MultipartFile file, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        if (file.isEmpty()) {
            return ResponseResult.createErrorResult("请选择要上传的文件！");
        }
        // 获取用户名
//        DiskUser user = WebUtil.getUserByRequest(request);
        String accessToken = request.getHeader("token");
        String s = redisUtil.get(accessToken);
        DiskUser diskUser = JSONUtil.toBean(JSONUtil.parseObj(s), DiskUser.class);
        if (diskUser != null) {
            // 上传文件/
            // 反馈用户信息
            return diskFileService.upload(file, diskUser, path);
        }
        return ResponseResult.createErrorResult("用户不存在！");
    }

    @GetMapping(value = "/test")
    public String test() {
        return diskFileService.getFileRootPath();
    }
}
