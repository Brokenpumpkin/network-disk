package com.bumpkin.disk.file.controller;

import cn.hutool.system.SystemUtil;
import com.bumpkin.disk.file.sevice.FileService;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 20:10
 */
@Slf4j
@RestController
public class FileController {

    @Value("${fileRootPath}")
    public String fileRootPath;

    @Autowired
    public FileService fileService;

    /**
     * 文件重命名 文件夹重命名时 老名字写path 新名字写newName oldName填@dir@
     */
    @PostMapping(value = "/fileRename")
    public ResponseResult fileRename(String oldName, String newName, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        if (oldName.isEmpty() || newName.isEmpty()) {
            return ResponseResult.createErrorResult("文件名字为空！");
        }
        //todo 获取用户名
        String userName = WebUtil.getUserNameByRequest(request);
        // 重命名文件
//        boolean b = fileService.userFileRename(oldName, newName, userName, path);
        String saveFilePath = fileRootPath + userName + "/" + path;
        String oldNameWithPath = saveFilePath + "/" + oldName;
        File file = new File(oldNameWithPath);
        if (fileService.userFileRename(oldName, newName, userName, path)) {
            log.warn("重命名成功！");
            return ResponseResult.createSuccessResult("重命名成功！");
        } else if (!file.exists()) {
            log.warn("没有重命名的权限！");
            return ResponseResult.createErrorResult("没有重命名的权限！");
        } else {
            log.warn("重命名失败！");
            return ResponseResult.createErrorResult("重命名失败！");
        }
    }

    /**
     * 创建文件夹
     * @param dirName
     * @param path
     * @param request
     * @return
     */
    @PostMapping(value = "/dirCreate")
    public ResponseResult dirCreate(String dirName, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        if (dirName.isEmpty() || path.isEmpty()) {
            return ResponseResult.createErrorResult("文件夹名字为空！");
        }
        //todo 获取用户名
        String userName = WebUtil.getUserNameByRequest(request);
        // path = /pan/userName/当前path
        if (!SystemUtil.getOsInfo().isWindows()) {
            path = "/pan/" + userName + path;
        } else {
            path = fileRootPath + userName + path;
        }

        // 重命名文件
        boolean b = fileService.userDirCreate(dirName, path);
        if (b) {
            return ResponseResult.createSuccessResult("文件夹创建成功！");
        } else {
            return ResponseResult.createErrorResult("文件夹创建失败！");
        }
    }

    @PostMapping(value = "fileMove")
    public ResponseResult fileMove(String fileName, String oldPath, String newPath, HttpServletRequest request) {
        if (fileName == null) {
            fileName = "@dir@";
        }
        if (oldPath.isEmpty() || newPath.isEmpty()) {
            return ResponseResult.createErrorResult("路径名字为空！");
        }
        //todo 获取用户名
        String userName = WebUtil.getUserNameByRequest(request);
        // 移动文件
        boolean b = fileService.userFileDirMove(fileName, oldPath, newPath, userName);
        if (b) {
            return ResponseResult.createSuccessResult("移动成功！");
        } else {
            return ResponseResult.createErrorResult("移动失败！");
        }
    }
}
