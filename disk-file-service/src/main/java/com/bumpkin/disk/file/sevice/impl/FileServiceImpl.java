package com.bumpkin.disk.file.sevice.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.file.dao.FileMapper;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.sevice.FileService;
import com.bumpkin.disk.file.util.MD5Util;
import com.bumpkin.disk.file.util.MyFileUtil;
import com.bumpkin.disk.file.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 11:04
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, DiskFile> implements FileService {

    @Value("${fileRootPath}")
    public String fileRootPath;//static

    @Autowired
    public IKeyGenerator iKeyGenerator;

//    @Value("${fileRootPath}")
//    public void setFileRootPath(String fileRootPath) {
//        FileServiceImpl.fileRootPath = fileRootPath;
//    }

    @Transactional
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
        assert saveFileName != null;
        String md5ToStr = MD5Util.getFileMD5ToString(new java.io.File(saveFilePath, saveFileName));
        if (!checkMd5Exist(md5ToStr)) {
            // 上传文件到-磁盘
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), new java.io.File(saveFilePath, saveFileName));
                DiskFile newFile = new DiskFile();
                String fileUuid = IdUtil.simpleUUID();
                newFile.setId(fileUuid);
                newFile.setFileId(fileUuid);
                newFile.setFileLocalLocation(saveFilePath);
                newFile.setFileSize((int) FileUtil.size(filePathDir));
                newFile.setFileMd5(md5ToStr);
                newFile.setFileType(StrUtil.subBefore(saveFileName, ".", false));
                newFile.setOriginalName(saveFileName);
                this.baseMapper.insert(newFile);
//                //虚拟地址
//                VirtualAddress virtualAddress = new VirtualAddress();
//                String virtualAddressUuid = IdUtil.simpleUUID();
//                virtualAddress.setId(virtualAddressUuid);
//                virtualAddress.setUuid(virtualAddressUuid);
//                virtualAddress.setFileId(fileUuid);
//                virtualAddress.setUserId();
//                virtualAddress.setFileName(saveFileName);
//                virtualAddress.setAddrType();
//                virtualAddress.setFileMd5(md5ToStr);
//                virtualAddress.setFileSize((int) FileUtil.size(filePathDir));
//                virtualAddress.setIsDir(0);
//                virtualAddress.setParentPath();
//                BaseEntity newEntity = EntityUtil.getNewEntity();
//                virtualAddress.setCreateTime(newEntity.getCreateTime());
//                virtualAddress.setUpdateTime(newEntity.getUpdateTime());
            } catch (Exception e) {
                log.error("Exception:", e);
                return false;
            }
        } else {
            //下载时如果有相同的md5值的文件则通过getFileByMd5()方法获取文件
            //todo 上传时如果有相同的md5值的文件就只创建一条虚拟地址记录
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
        return MyFileUtil.renameFile(oldNameWithPath, newNameWithPath);
    }

    @Override
    public Boolean userDirCreate(String dirName, String path) {
        java.io.File file = new java.io.File(path + "/" + dirName);
        return file.mkdir();
    }

    @Override
    public Boolean userFileDirMove(String fileName, String oldPath, String newPath, String userName) {
        return null;
    }

    @Override
    public String getFileRootPath() {
        return fileRootPath;
    }

    private Boolean checkMd5Exist(String md5ToStr) {
        QueryWrapper<DiskFile> wrapper = new QueryWrapper<>();
        wrapper.eq("file_md5", md5ToStr);
        DiskFile selectFile = this.baseMapper.selectOne(wrapper);
        return selectFile != null;
    }
}
