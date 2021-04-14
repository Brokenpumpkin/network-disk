package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.file.entity.LinkSecret;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 22:55
 */
public interface LinkSecretService extends IService<LinkSecret> {
    LinkSecret findLinkSecretByLocalLinkAndUserName(String localLink, String userName);
    Date updateExpireDay(LinkSecret linkSecret, Date date);
    Date updateShareDate(LinkSecret linkSecret, Date date);
}
