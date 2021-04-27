package com.bumpkin.disk.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 22:45
 */
@Data
@TableName(value = "link_secret")
public class LinkSecret {

    private String id;

    private String fileId;

    private String fileName;

    private String userId;

    private String localLink;

    private Date expireDate;

    private String secret;

    /**
     * 下载次数
     */
    private int downloadNum;

    private String secretLink;

    /**
     * 分享时间
     */
    private Date shareDate;
}
