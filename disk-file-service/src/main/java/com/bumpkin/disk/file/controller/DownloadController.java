package com.bumpkin.disk.file.controller;

import com.bumpkin.disk.file.sevice.FileService;
import com.bumpkin.disk.file.util.FileSplitUtil;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 20:13
 */
@Slf4j
@RestController
@RequestMapping("/download")
public class DownloadController {

    @Value("${fileRootPath}")
    public String fileRootPath;

    //MD5文件的大小
    public static int size;

    @Autowired
    public FileService fileService;

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
    public ResponseResult download(@RequestParam String fileName, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        if (fileName.isEmpty()) {
            return ResponseResult.createErrorResult("文件名字为空！");
        }
        //todo 获取用户名
        String userName = WebUtil.getUserNameByRequest(request);
        //        String userName ="zc";
        // 下载文件，获取下载路径,这个是 个映射的路径
        String link = fileService.download(fileName, userName, path);
        try {
            //这里校验要填真实的路经
            String newLink = link.replace("/data/", fileRootPath);
            String[] md5Array = FileSplitUtil.splitBySizeSubSection(newLink, size,
                    fileRootPath + "/tempMd5/" + userName + "/");
//            result.setObj(md5Array);
            if (!link.isEmpty()) {
                return ResponseResult.createSuccessResult(md5Array,link);
            } else {
                log.warn("下载失败");
                return ResponseResult.createErrorResult("");
            }
        } catch (Exception e) {
            log.error("Exception:", e);
            return ResponseResult.createErrorResult("");
        }

    }

    @GetMapping(value = "/test")
    public String test(@RequestParam String fileName, String path, HttpServletRequest request) {
        String userName = WebUtil.getUserNameByRequest(request);
        String link = fileService.download(fileName, userName, path);

        return link;
    }
}
