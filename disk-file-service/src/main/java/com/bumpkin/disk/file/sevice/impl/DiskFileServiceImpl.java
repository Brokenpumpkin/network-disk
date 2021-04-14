package com.bumpkin.disk.file.sevice.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.dao.DiskFileMapper;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.sevice.DiskFileService;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.file.util.MD5Util;
import com.bumpkin.disk.file.util.MultipartFileUtil;
import com.bumpkin.disk.file.util.MyFileUtil;
import com.bumpkin.disk.file.util.StringUtil;
import com.bumpkin.disk.file.vo.DiskFileVo;
import com.bumpkin.disk.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 11:04
 */
@Slf4j
@Service
public class DiskFileServiceImpl extends ServiceImpl<DiskFileMapper, DiskFile> implements DiskFileService {

    @Value("${fileRootPath}")
    public String fileRootPath;//static

    @Autowired
    public VirtualAddressService virtualAddressService;


//    @Value("${fileRootPath}")
//    public void setFileRootPath(String fileRootPath) {
//        FileServiceImpl.fileRootPath = fileRootPath;
//    }

    @Transactional
    @Override
    public ResponseResult upload(MultipartFile file, DiskUser diskUser, String path) {
        String userName = "null";
        String userId = "0";
        if (diskUser != null) {
            userName = diskUser.getUsername();
            userId = diskUser.getUserId();
        }
        // 文件虚拟地址路径
//        String saveFilePath = fileRootPath + userName + "/" + path;
        String saveFilePath = fileRootPath;
        log.warn("1 saveFilePath:" + saveFilePath);
        try {
            File newfile = MultipartFileUtil.multipartFileToFile(file);
            String saveFileName = file.getOriginalFilename();
            String md5ToStr = MD5Util.getFileMD5ToString(newfile);
            DiskFile diskFile = checkMd5Exist(md5ToStr);

            if (diskFile != null) {
                virtualAddressService.add(diskFile, userId, path);
                return ResponseResult.createSuccessResult("上传成功！");
            }
            assert saveFileName != null;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(saveFilePath, saveFileName));

            DiskFile newFile = new DiskFile();
            String fileUuid = IdUtil.simpleUUID();
            newFile.setId(fileUuid);
            newFile.setFileId(fileUuid);
            newFile.setFileLocalLocation(saveFilePath);
            newFile.setFileSize((int) FileUtil.size(newfile));
            newFile.setFileMd5(md5ToStr);
            newFile.setFileType(StrUtil.subBefore(saveFileName, ".", false));
            newFile.setOriginalName(saveFileName);
            this.baseMapper.insert(newFile);

            MultipartFileUtil.delteTempFile(newfile);
            return ResponseResult.createSuccessResult("上传成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.createErrorResult("上传失败！");
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
    public String fileShareCodeEncode(String filePathAndName) {
        return null;
    }

    @Override
    public List<DiskFileVo> userFileList(String userName, String path) {
        return null;
    }

    @Override
    public List<DiskFileVo> search(String key, String userName, String path) {
        return null;
    }

    @Override
    public String getFileRootPath() {
        return fileRootPath;
    }

    private DiskFile checkMd5Exist(String md5ToStr) {
        QueryWrapper<DiskFile> wrapper = new QueryWrapper<>();
        wrapper.eq("file_md5", md5ToStr);
        return this.baseMapper.selectOne(wrapper);
    }
}
