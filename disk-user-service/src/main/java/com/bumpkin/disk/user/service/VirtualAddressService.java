package com.bumpkin.disk.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.user.entity.VirtualAddress;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/22 20:54
 */
public interface VirtualAddressService extends IService<VirtualAddress> {
    Boolean addUserRootDir(DiskUser diskUser);
}
