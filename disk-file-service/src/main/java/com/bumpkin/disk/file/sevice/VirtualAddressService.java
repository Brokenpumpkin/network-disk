package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.VirtualAddress;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/14 14:31
 */
public interface VirtualAddressService extends IService<VirtualAddress> {
    void add(DiskFile diskFile, String userId, String parentPath);
}
