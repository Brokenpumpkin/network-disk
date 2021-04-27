package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.file.entity.LinkSecret;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 22:55
 */
public interface LinkSecretService extends IService<LinkSecret> {

    /**
     * 根据本地路径和用户名获取分享链接
     * @param localLink
     * @param userId
     * @return
     */
    LinkSecret findLinkSecretByLocalLinkAndUserId(String localLink, String userId);

    /**
     *
     * @param secretLink
     * @return
     */
    LinkSecret findLinkSecretBySecretLink(String secretLink);

    /**
     *
     * @param linkSecret
     * @param date
     * @return
     */
    Date updateExpireDay(LinkSecret linkSecret, Date date);
    Date updateShareDate(LinkSecret linkSecret, Date date);
}
