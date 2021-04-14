package com.bumpkin.disk.file.controller;

import cn.hutool.system.SystemUtil;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.util.MyFileUtil;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 20:10
 */
@Api(tags = "文件")
@Slf4j
@RestController
@RequestMapping(value = "/file")
public class FileController {

    @Value("${fileRootPath}")
    public String fileRootPath;

    @Autowired
    public DiskFileService diskFileService;

    /**
     * 文件重命名 文件夹重命名时 老名字写path 新名字写newName oldName填@dir@
     */
    @ApiOperation(value = "文件重命名")
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
        if (diskFileService.userFileRename(oldName, newName, userName, path)) {
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

    @ApiOperation(value = "创建文件夹")
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
        boolean b = diskFileService.userDirCreate(dirName, path);
        if (b) {
            return ResponseResult.createSuccessResult("文件夹创建成功！");
        } else {
            return ResponseResult.createErrorResult("文件夹创建失败！");
        }
    }

    @ApiOperation(value = "移动用户文件")
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
        boolean b = diskFileService.userFileDirMove(fileName, oldPath, newPath, userName);
        if (b) {
            return ResponseResult.createSuccessResult("移动成功！");
        } else {
            return ResponseResult.createErrorResult("移动失败！");
        }
    }

    @ApiOperation(value = "获取用户可用网盘空间大小")
    @GetMapping(value = "/getUserSpaceSize")
    public ResponseResult getUserSpaceSize(HttpServletRequest request) {
        // 普通用户限制80G，guest用户限制40G，
        String userName = WebUtil.getUserNameByRequest(request);
        Map<String, String> spaceMap = new HashMap<>();
        spaceMap.put("totalSpace", "80");
        double totalSpace = 80;
        if ("guest".equals(userName)) {
            spaceMap.put("totalSpace", "40");
            totalSpace = 40;
        }
        long dirlength = MyFileUtil.getDirSpaceSize(fileRootPath + userName);
        double dirlengthDouble = dirlength / 1024.0 / 1024 / 1024;
        String usedeSpace = String.format("%.2f", dirlengthDouble);
        log.warn("usedeSpace:{}", usedeSpace);
        String freeSpace = String.format("%.2f", totalSpace - Double.parseDouble(usedeSpace));
        log.warn("freeSpace:{}", freeSpace);
        spaceMap.put("freeSpace", freeSpace);

        return ResponseResult.createSuccessResult(spaceMap);
    }

    @ApiOperation(value = "获取用户所有文件目录")
    @GetMapping(value = "/gerUserFileList")
    public ResponseResult gerUserFileList() {
        // todo 编写获取用户所有文件目录
        return null;
    }

    public ResponseResult fileDelete(String fileName, String path, HttpServletRequest request) {

        return null;
    }
}
