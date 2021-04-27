package com.bumpkin.disk.file.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.file.dao.LinkSecretMapper;
import com.bumpkin.disk.file.dto.CheckSecretDto;
import com.bumpkin.disk.file.dto.CheckShareLinkDto;
import com.bumpkin.disk.file.entity.LinkSecret;
import com.bumpkin.disk.file.sevice.LinkSecretService;
import com.bumpkin.disk.file.util.EncryptUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 22:56
 */
@Service
public class LinkSecretServiceImpl extends ServiceImpl<LinkSecretMapper, LinkSecret> implements LinkSecretService {

    @Override
    public LinkSecret getLinkSecretByLocalLinkAndUserId(String localLink, String userId) {
        QueryWrapper<LinkSecret> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("local_link", localLink)
                .eq("user_id", userId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean checkShareLink(CheckShareLinkDto checkShareLinkDto, String userId) {
        QueryWrapper<LinkSecret> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("secret_link", checkShareLinkDto.getLink())
                .eq("user_id", userId)
                .eq("local_link", checkShareLinkDto.getPath() + "/" + checkShareLinkDto.getFileName());
        return this.baseMapper.selectOne(queryWrapper) != null;
    }

    @Override
    public Boolean checkShareFileSecret(CheckSecretDto checkSecretDto, String userId) {
        QueryWrapper<LinkSecret> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("secret", checkSecretDto.getSecret())
                    .eq("user_id", userId)
                    .eq("local_link", checkSecretDto.getPath() + "/" + checkSecretDto.getFileName());
        return this.baseMapper.selectOne(queryWrapper) != null;
    }

    @Override
    public String fileShareCodeEncode(String filePathAndName) {
        EncryptUtil des;
        try {
            // 自定义密钥
            String key = "aosdifu234oiu348f";
            des = new EncryptUtil(key, "utf-8");
            return des.encode(filePathAndName);
        } catch (Exception e) {
            log.error("Exception:", e);
        }
        return "null";
    }

    @Override
    public Date updateExpireDay(LinkSecret linkSecret, Date date) {
        UpdateWrapper<LinkSecret> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("local_link", linkSecret.getFilePath());
        linkSecret.setExpireDate(date);
        this.baseMapper.update(linkSecret, updateWrapper);
        return date;
    }

    @Override
    public Date updateShareDate(LinkSecret linkSecret, Date date) {
        UpdateWrapper<LinkSecret> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("local_link", linkSecret.getFilePath());
        linkSecret.setShareDate(date);
        this.baseMapper.update(linkSecret, updateWrapper);
        return date;
    }
}
