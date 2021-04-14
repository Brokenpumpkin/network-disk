package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.vo.DiskFileVo;
import com.bumpkin.disk.result.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 11:04
 */
public interface DiskFileService extends IService<DiskFile> {

    /**
     * 上传文件
     * @param file
     * @param diskUser
     * @param path
     * @return
     */
    ResponseResult upload(MultipartFile file, DiskUser diskUser, String path);

    /**
     * 下载文件
     * @param fileName
     * @param userName
     * @param path
     * @return
     */
    String download(String fileName, String userName, String path);

    /**
     * 重命名文件
     * @param oldName
     * @param newName
     * @param userName
     * @param path
     * @return
     */
    Boolean userFileRename(String oldName, String newName, String userName, String path);

    /**
     * 新建文件夹
     * @param dirName
     * @param path
     * @return
     */
    Boolean userDirCreate(String dirName, String path);

    /**
     * 移动文件、文件夹
     * @param fileName
     * @param oldPath
     * @param newPath
     * @param userName
     * @return
     */
    Boolean userFileDirMove(String fileName, String oldPath, String newPath, String userName);

    /**
     * 文件提取码-生成
     * @param filePathAndName
     * @return
     */
    String fileShareCodeEncode(String filePathAndName);

    /**
     * 列出用户文件
     * @param userName
     * @param path
     * @return
     */
    List<DiskFileVo> userFileList(String userName, String path);

    /**
     * 搜索文件
     * @param key
     * @param userName
     * @param path
     * @return
     */
    List<DiskFileVo> search(String key, String userName, String path);

    String getFileRootPath();
}
