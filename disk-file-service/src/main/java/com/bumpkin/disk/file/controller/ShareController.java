package com.bumpkin.disk.file.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.dto.CheckSecretDto;
import com.bumpkin.disk.file.dto.CheckShareLinkDto;
import com.bumpkin.disk.file.entity.LinkSecret;
import com.bumpkin.disk.file.entity.VirtualAddress;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.LinkSecretService;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.file.util.PassWordCreateUtil;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.spi.ResolveResult;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/11 14:42
 */
@Api(tags = "分享文件")
@Slf4j
@RestController
@RequestMapping(value = "/share")
public class ShareController {

    @Value("${fileRootPath}")
    public String fileRootPath;

    @Autowired
    private WebUtil webUtil;

    @Autowired
    private VirtualAddressService virtualAddressService;

    @Autowired
    private LinkSecretService linkSecretService;

    @ApiOperation(value = "验证提取码是否正确")
    @PostMapping(value = "/checkSecret")
    public ResponseResult checkSecret(@RequestBody CheckSecretDto checkSecretDto, HttpServletRequest request, BindingResult results) {
        if (results.hasErrors()) {
            return  ResponseResult.createErrorResult(results.getFieldError().getDefaultMessage());
        }
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        if (!linkSecretService.checkShareFileSecret(checkSecretDto, diskUser.getUserId())) {
            return ResponseResult.createErrorResult("提取码不正确！");
        }
        return ResponseResult.createSuccessResult("提取码正确！");

    }

    @ApiOperation(value = "验证分享链接后缀")
    @PostMapping(value = "/checkShareLink")
    public ResponseResult checkShareLink(@RequestBody CheckShareLinkDto checkShareLinkDto, HttpServletRequest request, BindingResult results) {
        if (results.hasErrors()) {
            return  ResponseResult.createErrorResult(results.getFieldError().getDefaultMessage());
        }
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        if (!linkSecretService.checkShareLink(checkShareLinkDto, diskUser.getUserId())) {
            return ResponseResult.createErrorResult("链接后缀不正确！");
        }
        return ResponseResult.createSuccessResult("链接后缀正确！");
    }

    @ApiOperation(value = "保存到我的网盘")
    @GetMapping(value = "/saveToDisk")
    public ResponseResult saveToDisk() {
        return null;
    }

    @ApiOperation(value = "文件提取码生成")
    @GetMapping(value = "/getShareLink")
    public ResponseResult getShareLink(@RequestParam String expireDay,
                                       @RequestParam String fileName,
                                       @RequestParam String path,
                                       HttpServletRequest request) {
        int expireDays = 1;
        if (expireDay != null) {
            if ("永久有效".equals(expireDay)) {
                expireDays = -1;
            } else {
                expireDays = Integer.parseInt(expireDay);
            }
        }
        if (path == null) {
            path = "/";
        }
        if (fileName.isEmpty() || path.isEmpty()) {
            return ResponseResult.createErrorResult("文件夹名字为空！");
        }
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        String filePathAndName = path + "/" + fileName;
        String b = linkSecretService.fileShareCodeEncode(filePathAndName);
        String secret = "";

        LinkSecret linkSecret = linkSecretService.getLinkSecretByLocalLinkAndUserId(filePathAndName, diskUser.getUserId());
        VirtualAddress virtualDiskFile = virtualAddressService.getDiskFileByFileNameAndParentPathAndUserId(fileName, path, diskUser.getUserId());
        if (linkSecret == null) {
            //设置提取密码
            int secretLen = 4;
            secret = PassWordCreateUtil.createPassWord(secretLen);
            linkSecret = new LinkSecret();
            linkSecret.setFilePath(filePathAndName);
            linkSecret.setFileId(virtualDiskFile.getFileId());
            linkSecret.setSecret(secret);
            linkSecret.setUserId(diskUser.getUserId());
            linkSecret.setDownloadNum(0);
            linkSecret.setFileName(fileName);

            if (expireDays != -1) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, expireDays);
                Date date = cal.getTime();
                linkSecret.setExpireDate(date);
            }
            linkSecret.setSecretLink(b);
            linkSecretService.save(linkSecret);
        } else {
            if (expireDays != -1) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, expireDays);
                Date date = cal.getTime();
                linkSecretService.updateExpireDay(linkSecret, date);
                linkSecretService.updateShareDate(linkSecret, new Date());
                secret = linkSecret.getSecret();
            } else {
                linkSecretService.updateExpireDay(linkSecret, null);
                linkSecretService.updateShareDate(linkSecret, new Date());
                secret = linkSecret.getSecret();
            }
        }
        if (SystemUtil.getOsInfo().isWindows()) {
            b = linkSecret.getSecretLink() + "#" + secret;
        } else {
            b = b + "#" + secret;
        }
        if (!"null".equals(b)) {
            return ResponseResult.createSuccessResult(b, "提取码生成成功");
        } else {
            return ResponseResult.createErrorResult("提取码生成失败！");
        }
    }
}
