package com.bumpkin.disk.file.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.util.FileSplitUtil;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.utils.RedisUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 20:13
 */
@Api(tags = "下载文件")
@Slf4j
@RestController
@RequestMapping("/download")
public class DownloadController {

    @Value("${fileRootPath}")
    public String fileRootPath;

    @Autowired
    public DiskFileService diskFileService;

    @Autowired
    private WebUtil webUtil;

    /**
     *
     * @param fileName
     * @param path
     * @param request
     * @param response
     * @throws FileNotFoundException
     */
    @GetMapping(value = "/fileDownload")
    public void download(@RequestParam String fileName, @RequestParam String path,
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (path == null) {
            path = "/";
        }
        //获取用户
        // 获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        // 下载文件
        diskFileService.download(fileName, diskUser, path, response);
    }

    @GetMapping(value = "/test")
    public String test(@RequestParam String fileName, String path, HttpServletRequest request) {
        String userName = WebUtil.getUserNameByRequest(request);
//        String link = diskFileService.download(fileName, userName, path);

        return "null";
    }
}
