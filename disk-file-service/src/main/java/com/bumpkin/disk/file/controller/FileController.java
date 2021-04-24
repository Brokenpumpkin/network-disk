package com.bumpkin.disk.file.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.dto.DirCreateDto;
import com.bumpkin.disk.file.dto.FileMoveDto;
import com.bumpkin.disk.file.dto.FileRenameDto;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.file.util.MyFileUtil;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.file.vo.DiskFileVo;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    public VirtualAddressService virtualAddressService;

    @Autowired
    private WebUtil webUtil;

    /**
     * 文件重命名 文件夹重命名时 老名字写path 新名字写newName oldName填@dir@
     */
    @ApiOperation(value = "文件重命名")
    @PostMapping(value = "/fileRename")
    public ResponseResult fileRename(@RequestBody FileRenameDto fileRenameDto, HttpServletRequest request) {
        if (fileRenameDto.getPath() == null) {
            fileRenameDto.setPath("/");
        }
        if (fileRenameDto.getOldName().isEmpty() || fileRenameDto.getNewName().isEmpty()) {
            return ResponseResult.createErrorResult("文件名字为空！");
        }
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);

        if (diskFileService.userFileRename(fileRenameDto.getOldName(), fileRenameDto.getNewName(), diskUser, fileRenameDto.getPath())) {
            log.warn("重命名成功！");
            return ResponseResult.createSuccessResult("重命名成功！");
        }
        return ResponseResult.createErrorResult("重命名失败！");

    }

    @ApiOperation(value = "创建文件夹")
    @PostMapping(value = "/dirCreate")
    public ResponseResult dirCreate(@RequestBody @Valid DirCreateDto dirCreateDto, HttpServletRequest request, BindingResult results) {
        if (results.hasErrors()) {
            return  ResponseResult.createErrorResult(results.getFieldError().getDefaultMessage());
        }
        if (dirCreateDto.getPath() == null) {
            dirCreateDto.setPath("/");
        }
        if (dirCreateDto.getDirName().isEmpty() || dirCreateDto.getPath().isEmpty()) {
            return ResponseResult.createErrorResult("文件夹名字为空！");
        }
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);

        // 创建文件夹
        boolean b = diskFileService.userDirCreate(dirCreateDto.getDirName(), dirCreateDto.getPath(), diskUser);
        if (b) {
            return ResponseResult.createSuccessResult("文件夹创建成功！");
        } else {
            return ResponseResult.createErrorResult("文件夹创建失败！");
        }
    }

    @ApiOperation(value = "移动用户文件")
    @PostMapping(value = "fileMove")
    public ResponseResult fileMove(@RequestBody @Valid FileMoveDto fileMoveDto, HttpServletRequest request, BindingResult results) {
        if (results.hasErrors()) {
            return  ResponseResult.createErrorResult(results.getFieldError().getDefaultMessage());
        }
        if (fileMoveDto.getFileName() == null) {
            fileMoveDto.setFileName("@dir@");
        }
        if (fileMoveDto.getOldPath().isEmpty() || fileMoveDto.getNewPath().isEmpty()) {
            return ResponseResult.createErrorResult("路径名字为空！");
        }
        // 获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        // 移动文件
        if (diskFileService.userFileDirMove(fileMoveDto.getFileName(), fileMoveDto.getOldPath(), fileMoveDto.getNewPath(), diskUser)) {
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

    @ApiOperation(value = "获取用户当前路径下文件目录")
    @GetMapping(value = "/gerUserFileList")
    public ResponseResult gerUserFileList(String path, HttpServletRequest request) {
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        List<DiskFileVo> diskFileVoList = diskFileService.userFileList(diskUser, path);
        return ResponseResult.createSuccessResult(diskFileVoList, "获取用户文件目录成功！");
    }

    @ApiOperation(value = "获取用户目录树形结构")
    @GetMapping(value = "/getUserFileListByRoot")
    public ResponseResult getUserFileListByRoot() {

        return null;
    }

    @ApiOperation(value = "删除用户文件")
    @DeleteMapping(value = "/fileDelete")
    public ResponseResult fileDelete(String fileName, String path, HttpServletRequest request) {
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        virtualAddressService.delFile(diskUser, fileName, path);
        return ResponseResult.createSuccessResult("文件删除成功！");
    }

    @GetMapping(value = "/search")
    public ResponseResult search(String keyword, HttpServletRequest request) {
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        List<DiskFileVo> diskFileVoList = diskFileService.search(keyword, diskUser);
        return ResponseResult.createSuccessResult(diskFileVoList, "文件搜索成功");
    }
}
