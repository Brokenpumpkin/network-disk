package com.bumpkin.disk.file.controller;

import cn.hutool.system.SystemUtil;
import com.bumpkin.disk.file.entity.LinkSecret;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.LinkSecretService;
import com.bumpkin.disk.file.util.PassWordCreateUtil;
import com.bumpkin.disk.file.util.StringUtil;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.result.ResponseResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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
    private DiskFileService diskFileService;

    @Autowired
    private LinkSecretService linkSecretService;

    @GetMapping(value = "/getShareLink")
    public ResponseResult getShareLink(@RequestParam String expireDay, String fileName, String path, HttpServletRequest request) {
        // todo 根据后续情况修改
        int expireDays = 3;
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
        // 获取用户名
        String userName = WebUtil.getUserNameByRequest(request);
        String filePathAndName = userName + "/" + path + "/" + fileName;
        filePathAndName = StringUtil.stringSlashToOne(filePathAndName);
        log.warn("filePathAndName:" + filePathAndName);
        String b = diskFileService.fileShareCodeEncode(filePathAndName);
        String secret = "";
        File file = new File(fileRootPath + "/" + filePathAndName);
        String localLink = "/data/share/" + filePathAndName;
        //存入数据库
        LinkSecret linkSecret = linkSecretService.findLinkSecretByLocalLinkAndUserName(localLink, userName);
        if (linkSecret == null) {
            //设置提取密码
            int secretLen = 4;
            secret = PassWordCreateUtil.createPassWord(secretLen);
            linkSecret = new LinkSecret();
            linkSecret.setLocalLink(localLink);
            linkSecret.setSecret(secret);
            linkSecret.setUserName(userName);
            linkSecret.setDownloadNum(0);
            linkSecret.setFileName(fileName);

            if (expireDays != -1) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, expireDays);
                Date date = cal.getTime();
                linkSecret.setExpireDate(date);
            }

            log.warn("b:" + b);
            linkSecret.setSecretLink(b);
            linkSecretService.save(linkSecret);
            //test
            LinkSecret linkSecretTemp = linkSecretService.findLinkSecretByLocalLinkAndUserName(localLink, userName);
            log.warn(linkSecretTemp.getSecretLink());
            //test

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
                secret = linkSecret.getSecret();
                linkSecretService.updateShareDate(linkSecret, new Date());
            }
        }
        if (SystemUtil.getOsInfo().isWindows()) {
            b = linkSecret.getSecretLink() + "##" + secret;
        } else {
            b = b + "##" + secret;
        }
        if (!"null".equals(b)) {
            return ResponseResult.createSuccessResult(b, "提取码生成成功");
        } else {
            return ResponseResult.createErrorResult("提取码生成失败！");
        }
    }
}
