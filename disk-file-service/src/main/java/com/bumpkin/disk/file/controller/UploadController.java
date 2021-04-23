package com.bumpkin.disk.file.controller;

import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 20:13
 */
@Api(tags = "上传文件")
@Slf4j
@RequestMapping("/upload")
@RestController
public class UploadController {

    @Autowired
    public DiskFileService diskFileService;

    @Autowired
    private WebUtil webUtil;

    @PostMapping(value = "/fileUpload")
    public ResponseResult upload(@RequestParam(name = "file") MultipartFile file
            ,@RequestParam("path") String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        if (file.isEmpty()) {
            return ResponseResult.createErrorResult("请选择要上传的文件！");
        }
        // 获取用户名
        DiskUser diskUser = webUtil.getUserByRequest(request);
        if (diskUser != null) {
            // 上传文件/
            // 反馈用户信息
            return diskFileService.upload(file, diskUser, path);
        }
        return ResponseResult.createErrorResult("用户不存在！");
    }

    @GetMapping(value = "/test")
    public String test(HttpServletRequest request) {
//        return diskFileService.getFileRootPath();
//        String accessToken = request.getHeader("Authorization");
//        String s = StrUtil.subAfter(accessToken, " ", false);
        DiskUser diskUser = webUtil.getUserByRequest(request);
        log.warn(request.getHeader("Authorization"));
//        log.warn(s);
        log.warn(diskUser.toString());
        return diskUser.toString();
    }
}
