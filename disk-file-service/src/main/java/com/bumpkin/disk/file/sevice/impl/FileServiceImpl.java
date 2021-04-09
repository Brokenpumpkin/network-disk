package com.bumpkin.disk.file.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.file.dao.FileMapper;
import com.bumpkin.disk.file.entity.File;
import com.bumpkin.disk.file.sevice.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 11:04
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Value("${fileRootPath}")
    public String fileRootPath;//static

//    @Value("${fileRootPath}")
//    public void setFileRootPath(String fileRootPath) {
//        FileServiceImpl.fileRootPath = fileRootPath;
//    }

    @Override
    public Boolean upload(MultipartFile file, String userName, String path) {
        // 服务器上传的文件所在路径
        String saveFilePath = fileRootPath + userName + "/" + path;
        log.warn("1 saveFilePath:" + saveFilePath);
        // 判断文件夹是否存在-建立文件夹
        java.io.File filePathDir = new java.io.File(saveFilePath);
        if (!filePathDir.exists()) {
            filePathDir.mkdir();
        }
        // 获取上传文件的原名 例464e7a80_710229096@qq.com.zip
        String saveFileName = file.getOriginalFilename();
        // 上传文件到-磁盘
        try {
            assert saveFileName != null;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new java.io.File(saveFilePath, saveFileName));
        } catch (Exception e) {
            log.error("Exception:", e);
            return false;
        }
        return true;
    }

    @Override
    public String getFileRootPath() {
        return fileRootPath;
    }
}
