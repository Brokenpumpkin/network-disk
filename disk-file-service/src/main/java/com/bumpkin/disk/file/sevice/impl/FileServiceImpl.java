package com.bumpkin.disk.file.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.file.dao.FileMapper;
import com.bumpkin.disk.file.entity.File;
import com.bumpkin.disk.file.sevice.FileService;
import com.bumpkin.disk.file.util.FileUtil;
import com.bumpkin.disk.file.util.StringUtil;
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
    public String download(String fileName, String userName, String path) {
        // 服务器下载的文件所在的本地路径的文件夹
        String saveFilePath = fileRootPath + userName + "/" + path;
        log.warn("1 saveFilePath:" + saveFilePath);
        // 判断文件夹是否存在-建立文件夹
        java.io.File filePathDir = new java.io.File(saveFilePath);
        if (!filePathDir.exists()) {
            filePathDir.mkdir();
        }
        // 本地路径
        saveFilePath = saveFilePath + "/" + fileName;
        String link = saveFilePath.replace(fileRootPath, "/data/");
        link = StringUtil.stringSlashToOne(link);
        log.warn("返回的路径：" + link);
        return link;
    }

    @Override
    public Boolean userFileRename(String oldName, String newName, String userName, String path) {
        // 重命名-本地磁盘文件
        String oldNameWithPath;
        String newNameWithPath;
        if ("@dir@".equals(oldName)) {
            oldNameWithPath = StringUtil.stringSlashToOne(fileRootPath + userName + "/" + path);
            newNameWithPath =
                    oldNameWithPath.substring(0, (int) StringUtil.getfilesuffix(oldNameWithPath, true, "/")) + "/" + newName;
            newNameWithPath = StringUtil.stringSlashToOne(newNameWithPath);
        } else {
            oldNameWithPath = StringUtil.stringSlashToOne(fileRootPath + userName + "/" + path + "/" + oldName);
            newNameWithPath = StringUtil.stringSlashToOne(fileRootPath + userName + "/" + path + "/" + newName);
        }
        return FileUtil.renameFile(oldNameWithPath, newNameWithPath);
    }

    @Override
    public Boolean userDirCreate(String dirName, String path) {
        java.io.File file = new java.io.File(path + "/" + dirName);
        return file.mkdir();
    }

    @Override
    public String getFileRootPath() {
        return fileRootPath;
    }
}
