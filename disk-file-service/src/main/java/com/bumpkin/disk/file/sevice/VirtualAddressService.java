package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.VirtualAddress;

import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/14 14:31
 */
public interface VirtualAddressService extends IService<VirtualAddress> {
    void addFile(DiskFile diskFile, String userId, String parentPath);
    Boolean addDir(String dirName, String parentPath, DiskUser diskUser);
    Boolean delFile(DiskUser diskUser, String fileName, String parentPath);
    Boolean fileDirVirtualAddressMove(String fileName, String oldPath, String newPath, DiskUser diskUser);
    List<VirtualAddress> getFileByUserAndParentPath(DiskUser diskUser, String parentPath);
    VirtualAddress getDiskFileByFileNameAndParentPathAndUserId(String fileName, String parentPath, String userId);
}
