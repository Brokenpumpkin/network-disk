package com.bumpkin.disk.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 10:54
 */
@Data
@TableName("file")
public class DiskFile {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件名
     */
    private String originalName;

    /**
     * 文件大小 bytes长度
     */
    private Integer fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件本地存储地址
     */
    private String fileLocalLocation;

    /**
     * 文件md5值
     */
    private String fileMd5;

    private Date createTime;
}
