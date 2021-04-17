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

    //MD5文件的大小
    public static int size;

    @Autowired
    public DiskFileService diskFileService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${size}")
    public void setSize(int size) {
        DownloadController.size = size;
    }

    /**
     *
     * @param fileName 文件名
     * @param path 该用户文件夹下的文件路径
     * @param request
     * @return 需要下载的文件在本地的路径
     */
    @GetMapping(value = "fileDownload")
    public void download(@RequestParam String fileName, String path,
                                   HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        if (path == null) {
            path = "/";
        }
        //获取用户
        String accessToken = StrUtil.subAfter(request.getHeader("Authorization"), " ", false);
        log.info(accessToken);
        String s = redisUtil.get(accessToken);
        DiskUser diskUser = JSONUtil.toBean(JSONUtil.parseObj(s), DiskUser.class);
        // 下载文件
        diskFileService.download(fileName, diskUser, path, response);
//        try {
//            //这里校验要填真实的路经
//            String newLink = link.replace("/data/", fileRootPath);
//            String[] md5Array = FileSplitUtil.splitBySizeSubSection(newLink, size,
//                    fileRootPath + "/tempMd5/" + userName + "/");
////            result.setObj(md5Array);
//            if (!link.isEmpty()) {
//                return ResponseResult.createSuccessResult(md5Array,link);
//            } else {
//                log.warn("下载失败");
//                return ResponseResult.createErrorResult("");
//            }
//        } catch (Exception e) {
//            log.error("Exception:", e);
//            return ResponseResult.createErrorResult("");
//        }

    }

    @GetMapping(value = "/test")
    public String test(@RequestParam String fileName, String path, HttpServletRequest request) {
        String userName = WebUtil.getUserNameByRequest(request);
//        String link = diskFileService.download(fileName, userName, path);

        return "null";
    }
}
