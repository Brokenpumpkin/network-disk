package com.bumpkin.disk.file.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 23:28
 */
@Data
public class DiskFileVo {
    /**
     * 文件名
     */
    private String fileName;

    private Integer isDir;

    /**
     * 文件大小
     */
    private String fileSize;

    private Date createTime;

    private Date updateTime;
}
