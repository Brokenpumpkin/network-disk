package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.file.entity.DiskFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 11:04
 */
public interface FileService extends IService<DiskFile> {

    /**
     *
     * @param file
     * @param userName
     * @param path
     * @return
     */
    Boolean upload(MultipartFile file, String userName, String path);

    /**
     *
     * @param fileName
     * @param userName
     * @param path
     * @return
     */
    String download(String fileName, String userName, String path);

    /**
     *
     * @param oldName
     * @param newName
     * @param userName
     * @param path
     * @return
     */
    Boolean userFileRename(String oldName, String newName, String userName, String path);

    Boolean userDirCreate(String dirName, String path);

    Boolean userFileDirMove(String fileName, String oldPath, String newPath, String userName);
    String getFileRootPath();
}
