package com.bumpkin.disk.file.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.dto.CheckSecretDto;
import com.bumpkin.disk.file.dto.CheckShareLinkDto;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.LinkSecret;
import com.bumpkin.disk.file.entity.VirtualAddress;
import com.bumpkin.disk.file.feign.UserService;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.LinkSecretService;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.file.util.PassWordCreateUtil;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.file.vo.ShareLinkVo;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.utils.FileEncAndDecUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.naming.spi.ResolveResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
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

    @Autowired
    private DiskFileService diskFileService;

    @Resource
    private UserService userService;

    @ApiOperation(value = "验证提取码是否正确")
    @PostMapping(value = "/checkSecret")
    public ResponseResult checkSecret(@RequestBody CheckSecretDto checkSecretDto, HttpServletRequest request, BindingResult results) {
        if (results.hasErrors()) {
            return  ResponseResult.createErrorResult(results.getFieldError().getDefaultMessage());
        }
        if (!linkSecretService.checkShareFileSecret(checkSecretDto)) {
            return ResponseResult.createErrorResult("提取码错误！");
        }
        return ResponseResult.createSuccessResult("提取码正确！");

    }

    @ApiOperation(value = "验证分享链接后缀")
    @PostMapping(value = "/checkShareLink")
    public ResponseResult checkShareLink(@RequestBody CheckShareLinkDto checkShareLinkDto, HttpServletRequest request, BindingResult results) {
        if (results.hasErrors()) {
            return  ResponseResult.createErrorResult(results.getFieldError().getDefaultMessage());
        }
        if (!linkSecretService.checkShareLink(checkShareLinkDto)) {
            return ResponseResult.createErrorResult("链接无效或已过期！");
        }
        LinkSecret linkSecret = linkSecretService.getLinkSecretBySecretLink(checkShareLinkDto.getLink());
        return ResponseResult.createSuccessResult(linkSecret, "链接后缀正确！");
    }

    @ApiOperation(value = "保存到我的网盘")
    @GetMapping(value = "/saveToDisk")
    public ResponseResult saveToDisk(String link, String path, HttpServletRequest request) {
        //获取用户
        DiskUser diskUser = webUtil.getUserByRequest(request);
        LinkSecret linkSecret = linkSecretService.getLinkSecretBySecretLink(link);
        DiskFile diskFile = diskFileService.getBaseMapper().selectById(linkSecret.getFileId());
        virtualAddressService.addFile(diskFile, diskUser.getUserId(), path);
        return ResponseResult.createSuccessResult("添加成功！");
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
//        String b = linkSecretService.fileShareCodeEncode(filePathAndName);
        String secretLink = IdUtil.fastSimpleUUID();
        String secret = "";

        LinkSecret linkSecret = linkSecretService.getLinkSecretByLocalLinkAndUserId(filePathAndName, diskUser.getUserId());
        VirtualAddress virtualDiskFile = virtualAddressService.getDiskFileByFileNameAndParentPathAndUserId(fileName, path, diskUser.getUserId());
        if (linkSecret == null) {
            //设置提取密码
            int secretLen = 4;
            secret = PassWordCreateUtil.createPassWord(secretLen);
            linkSecret = new LinkSecret();
            linkSecret.setId(secretLink);
            linkSecret.setFilePath(filePathAndName);
            linkSecret.setFileId(virtualDiskFile.getFileId());
            linkSecret.setSecret(secret);
            linkSecret.setUserId(diskUser.getUserId());
            linkSecret.setDownloadNum(0);
            linkSecret.setFileName(fileName);
            linkSecret.setSecretLink(secretLink);
            if (expireDays != -1) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, expireDays);
                Date date = cal.getTime();
                linkSecret.setExpireDate(date);
            }
            linkSecretService.save(linkSecret);
        } else {
            if (expireDays == -1) {
                linkSecretService.updateExpireDay(linkSecret, null);
                linkSecretService.updateShareDate(linkSecret, new Date());
                secret = linkSecret.getSecret();
                secretLink = linkSecret.getSecretLink();
            } else {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, expireDays);
                Date date = cal.getTime();
                linkSecretService.updateExpireDay(linkSecret, date);
                linkSecretService.updateShareDate(linkSecret, new Date());
                secret = linkSecret.getSecret();
                secretLink = linkSecret.getSecretLink();
            }
        }
        ShareLinkVo shareLinkVo = new ShareLinkVo();
        shareLinkVo.setSecretLink(secretLink);
        shareLinkVo.setSecret(secret);
        return ResponseResult.createSuccessResult(shareLinkVo, "提取码生成成功");
    }

    @ApiOperation(value = "分享下载")
    @GetMapping(value = "/shareDownload")
    public void shareDownload(String link, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LinkSecret linkSecret = linkSecretService.getLinkSecretBySecretLink(link);
        DiskFile diskFile = diskFileService.getBaseMapper().selectById(linkSecret.getFileId());
        String fileLocalLocation = diskFile.getFileLocalLocation();

        DiskUser diskUser = userService.getUserById(linkSecret.getUserId());
        // 文件解密
        Key key = FileEncAndDecUtil.toKey(diskUser.getPassword());
        File encfile = new File(fileLocalLocation + diskFile.getSaveFileName());
        File decFile = new File(fileLocalLocation + diskFile.getOriginalName());
        FileEncAndDecUtil.decFile(encfile, decFile, key, diskUser.getSalt().getBytes(StandardCharsets.UTF_8));

        byte[] bytes = FileUtils.readFileToByteArray(decFile);
        MagicMatch magicMatch = Magic.getMagicMatch(decFile, true, false);

        response.setStatus(200);
        response.setContentType(magicMatch.getMimeType());
        response.setHeader("Access-Control-Expose-Headers", "fileName");
        response.setHeader("fileName", URLEncoder.encode(diskFile.getOriginalName(), "UTF-8"));
//        response.setHeader("Access-Control-Expose-Headers", "type");
//        response.setHeader("type", magicMatch.getMimeType());
        response.setHeader("Accept-Ranges", "bytes");
        //new String(fileName.getBytes(), StandardCharsets.UTF_8)
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(diskFile.getOriginalName(), "UTF-8"));
        response.getOutputStream().write(bytes);
    }
}
