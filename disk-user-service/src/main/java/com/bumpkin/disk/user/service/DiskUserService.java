package com.bumpkin.disk.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.user.dto.DiskUserRegisterDto;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/12 16:01
 */
public interface DiskUserService extends IService<DiskUser> {

    DiskUser getUserByPhone(String phoneNum);
    DiskUser getUserByUsername(String username);
    ResponseResult add(DiskUserRegisterDto userRegisterDto);
}
