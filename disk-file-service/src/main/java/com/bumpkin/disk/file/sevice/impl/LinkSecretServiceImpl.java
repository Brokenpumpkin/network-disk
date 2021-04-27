package com.bumpkin.disk.file.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.file.dao.LinkSecretMapper;
import com.bumpkin.disk.file.entity.LinkSecret;
import com.bumpkin.disk.file.sevice.LinkSecretService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 22:56
 */
@Service
public class LinkSecretServiceImpl extends ServiceImpl<LinkSecretMapper, LinkSecret> implements LinkSecretService {

    @Override
    public LinkSecret findLinkSecretByLocalLinkAndUserId(String localLink, String userId) {
        QueryWrapper<LinkSecret> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("local_link", localLink)
                .eq("user_id", userId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public LinkSecret findLinkSecretBySecretLink(String secretLink) {
        return null;
    }

    @Override
    public Date updateExpireDay(LinkSecret linkSecret, Date date) {
        UpdateWrapper<LinkSecret> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("local_link", linkSecret.getLocalLink());
        linkSecret.setExpireDate(date);
        this.baseMapper.update(linkSecret, updateWrapper);
        return date;
    }

    @Override
    public Date updateShareDate(LinkSecret linkSecret, Date date) {
        UpdateWrapper<LinkSecret> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("local_link", linkSecret.getLocalLink());
        linkSecret.setShareDate(date);
        this.baseMapper.update(linkSecret, updateWrapper);
        return date;
    }
}
