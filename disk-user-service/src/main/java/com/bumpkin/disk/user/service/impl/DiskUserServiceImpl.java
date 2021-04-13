package com.bumpkin.disk.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.result.ResponseResult;
import com.bumpkin.disk.user.dao.DiskUserMapper;
import com.bumpkin.disk.user.dto.DiskUserRegisterDto;
import com.bumpkin.disk.user.service.DiskUserService;
import com.bumpkin.disk.entities.DiskUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/12 16:01
 */
@Service
public class DiskUserServiceImpl extends ServiceImpl<DiskUserMapper, DiskUser> implements DiskUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    public ResponseResult add(DiskUserRegisterDto userRegisterDto) {
        if (this.getUserByUsername(userRegisterDto.getUsername()) != null) {
            return ResponseResult.createErrorResult("用户名已存在！");
        }
        if (this.getUserByPhone(userRegisterDto.getPhone()) != null) {
            return ResponseResult.createErrorResult("手机号码已存在！");
        }
        DiskUser diskUser = new DiskUser();
        String id = IdUtil.simpleUUID();
        diskUser.setId(id);
        diskUser.setUserId(id);
        diskUser.setUsername(userRegisterDto.getUsername());
        diskUser.setAlias(userRegisterDto.getAlias());
        diskUser.setEmail(userRegisterDto.getEmail());
        diskUser.setLevel(userRegisterDto.getLevel());
        diskUser.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        diskUser.setPhone(userRegisterDto.getPhone());
        this.baseMapper.insert(diskUser);
        return ResponseResult.createSuccessResult("操作成功！");
    }
}
