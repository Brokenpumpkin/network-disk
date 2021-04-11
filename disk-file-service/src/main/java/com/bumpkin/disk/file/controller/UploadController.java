package com.bumpkin.disk.file.controller;

import com.bumpkin.disk.file.sevice.FileService;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 20:13
 */
@RequestMapping("/upload")
@RestController
public class UploadController {

    @Autowired
    public FileService fileService;

    @PostMapping(value = "/fileUpload")
    public ResponseResult upload(@RequestParam MultipartFile file, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        if (file.isEmpty()) {
            return ResponseResult.createErrorResult("请选择要上传的文件！");
        }
        //todo 获取用户名
        String userName = WebUtil.getUserNameByRequest(request);
        // 上传文件
        // 反馈用户信息
        if (fileService.upload(file, userName, path)) {
            return ResponseResult.createSuccessResult("上传成功！");
        }
        return ResponseResult.createErrorResult("上传失败！");
    }

    @GetMapping(value = "/test")
    public String test() {
        return fileService.getFileRootPath();
    }
}
