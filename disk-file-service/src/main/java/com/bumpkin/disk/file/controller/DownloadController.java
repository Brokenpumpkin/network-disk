package com.bumpkin.disk.file.controller;

import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.VirtualAddress;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.file.util.WebUtil;
import com.bumpkin.disk.utils.FileEncAndDecUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;

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
    public VirtualAddressService virtualAddressService;

    @Autowired
    private WebUtil webUtil;


//    @GetMapping(value = "/fileDownload")
//    public void download(@RequestParam String fileName, @RequestParam String path,
//                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
//        if (path == null) {
//            path = "/";
//        }
//        //获取用户
//        DiskUser diskUser = webUtil.getUserByRequest(request);
//        // 下载文件
//        diskFileService.download(fileName, diskUser, path, response);
//    }

    @GetMapping(value = "/test")
    public String test(@RequestParam String fileName, String path, HttpServletRequest request) {
        String userName = WebUtil.getUserNameByRequest(request);
//        String link = diskFileService.download(fileName, userName, path);

        return "null";
    }

    @GetMapping(value = "/download")
    public void download(@RequestParam String fileName, @RequestParam String path, HttpServletRequest req, HttpServletResponse response){
        DiskUser diskUser = webUtil.getUserByRequest(req);
        VirtualAddress virtualAddress = virtualAddressService.getDiskFileByFileNameAndParentPathAndUserId(fileName, path, diskUser.getUserId());
        String fileId = virtualAddress.getFileId();
        DiskFile diskFile = diskFileService.getBaseMapper().selectById(fileId);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        OutputStream fos = null;

        File encFile = new File(diskFile.getFileLocalLocation() + diskFile.getSaveFileName());
        File decFile = new File(diskFile.getFileLocalLocation() + diskFile.getOriginalName());
        try {
            // 文件解密
            Key key = FileEncAndDecUtil.toKey(diskFile.getFirstUploaderPassword());
            FileEncAndDecUtil.decFile(encFile, decFile, key, diskFile.getFirstUploaderSalt().getBytes(StandardCharsets.UTF_8));
//            File file = new File(fileLocalLocation);
//            bis = new BufferedInputStream(new FileInputStream(fileLocalLocation));
            bis = new BufferedInputStream(new FileInputStream(decFile));
            fos = response.getOutputStream();
            bos = new BufferedOutputStream(fos);
            MagicMatch magicMatch = Magic.getMagicMatch(decFile, true, false);
            response.setStatus(200);
            response.setContentType(magicMatch.getMimeType());
            response.setHeader("Access-Control-Expose-Headers", "fileName");
            response.setHeader("fileName", URLEncoder.encode(virtualAddress.getUserFileName(), "UTF-8"));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(virtualAddress.getUserFileName(), "UTF-8"));
            int byteRead = 0;
            byte[] buffer = new byte[8192];
            while ((byteRead = bis.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, byteRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bis.close();
                fos.close();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            decFile.delete();
        }
    }
}
