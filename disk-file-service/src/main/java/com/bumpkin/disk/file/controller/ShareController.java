package com.bumpkin.disk.file.controller;

import cn.hutool.system.SystemUtil;
import com.bumpkin.disk.entities.DiskUser;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private DiskFileService diskFileService;

    @Autowired
    private VirtualAddressService virtualAddressService;

    @Autowired
    private LinkSecretService linkSecretService;

    @ApiOperation(value = "shareCallBack(验证提取码是否正确)")
    @GetMapping(value = "/fileShareCallback")
    public ResponseResult fileShareCallback(String link) {
        log.warn("执行shareCallBack接口！：" + link);
        if (link.isEmpty()) {
            return ResponseResult.createErrorResult("提取码为空");
        }
        String downloadLink = diskFileService.fileShareCodeDecode(link);
        log.warn("downloadLink:" + downloadLink);
        if (!"null".equals(downloadLink)) {
            return ResponseResult.createSuccessResult(downloadLink, "提取码正确");
        } else {
            return ResponseResult.createErrorResult("提取码不正确！");
        }
    }

    @ApiOperation(value = "文件提取码生成")
    @GetMapping(value = "/getShareLink")
    public ResponseResult getShareLink(@RequestParam String expireDay,
                                       @RequestParam String fileName,
                                       @RequestParam String path,
                                       HttpServletRequest request) {
        int expireDays = 1;
        if (expireDay != null) {
            if (expireDay.equals("永久有效")) {
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
        String localLink = "/data/share/" + filePathAndName;
        log.warn("filePathAndName:" + filePathAndName);
        String b = diskFileService.fileShareCodeEncode(filePathAndName);
        String secret = "";

        LinkSecret linkSecret = linkSecretService.findLinkSecretByLocalLinkAndUserId(localLink, diskUser.getUserId());
        VirtualAddress virtualDiskFile = virtualAddressService.getDiskFileByFileNameAndParentPathAndUserId(fileName, path, diskUser.getUserId());
        if (linkSecret == null) {
            //设置提取密码
            int secretLen = 4;
            secret = PassWordCreateUtil.createPassWord(secretLen);
            linkSecret = new LinkSecret();
            linkSecret.setLocalLink(localLink);
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
