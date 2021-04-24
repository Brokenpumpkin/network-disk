package com.bumpkin.disk.file.controller;

import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @ApiOperation(value = "一次性上传")
    @PostMapping(value = "/fileUpload")
    public ResponseResult upload(@RequestParam(name = "file") MultipartFile file
            ,@RequestParam("path") String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        if (file.isEmpty()) {
            return ResponseResult.createErrorResult("请选择要上传的文件！");
        }
        // 获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        if (diskUser != null) {
            // 上传文件/
            // 反馈用户信息
            return diskFileService.upload(file, diskUser, path);
        }
        return ResponseResult.createErrorResult("用户不存在！");
    }

    @ApiOperation(value = "分块上传 有断点续传的功能")
    @PostMapping(value = "/uploadServlet")
    public void uploadServlet(HttpServletRequest request, HttpServletResponse response, MultipartFile file, String path) {

    }

    @ApiOperation(value = "上传之前检查")
    @PostMapping(value = "/check")
    public ResponseResult checkChunk(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @ApiOperation(value = "所有分块上传完成后合并")
    @GetMapping(value = "/merge")
    public void mergeChunks(HttpServletRequest request, HttpServletResponse response, String path) {

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
