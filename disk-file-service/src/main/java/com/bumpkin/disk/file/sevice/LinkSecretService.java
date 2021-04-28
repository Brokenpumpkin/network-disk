package com.bumpkin.disk.file.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bumpkin.disk.file.dto.CheckSecretDto;
import com.bumpkin.disk.file.dto.CheckShareLinkDto;
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
    LinkSecret getLinkSecretByLocalLinkAndUserId(String localLink, String userId);

    LinkSecret getLinkSecretBySecretLink(String link);

    /**
     * 文件提取码-生成
     * @param filePathAndName
     * @return
     */
    String fileShareCodeEncode(String filePathAndName);

    Boolean checkShareFileSecret(CheckSecretDto checkSecretDto);

    Boolean checkShareLink(CheckShareLinkDto checkShareLinkDto);

    Date updateExpireDay(LinkSecret linkSecret, Date date);

    Date updateShareDate(LinkSecret linkSecret, Date date);
}
