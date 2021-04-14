package com.bumpkin.disk.file.sevice.impl;

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
    public LinkSecret findLinkSecretByLocalLinkAndUserName(String localLink, String userName) {
        return null;
    }

    @Override
    public Date updateExpireDay(LinkSecret linkSecret, Date date) {
        return null;
    }

    @Override
    public Date updateShareDate(LinkSecret linkSecret, Date date) {
        return null;
    }
}
