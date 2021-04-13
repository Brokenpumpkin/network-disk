package com.bumpkin.disk.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.auth.dao.DiskUserMapper;
import com.bumpkin.disk.auth.service.DiskUserService;
import com.bumpkin.disk.entities.DiskUser;
import org.springframework.stereotype.Service;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/12 16:01
 */
@Service
public class DiskUserServiceImpl extends ServiceImpl<DiskUserMapper, DiskUser> implements DiskUserService {

    @Override
    public DiskUser getUserByPhone(String phoneNum) {
        QueryWrapper<DiskUser> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phoneNum);
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public DiskUser getUserByUsername(String username) {
        QueryWrapper<DiskUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return this.baseMapper.selectOne(wrapper);
    }
}
