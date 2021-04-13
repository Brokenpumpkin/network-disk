package com.bumpkin.disk.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.entities.DiskUser;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/12 16:01
 */
public interface DiskUserService extends IService<DiskUser> {

    DiskUser getUserByPhone(String phoneNum);
    DiskUser getUserByUsername(String username);
}
