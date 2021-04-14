package com.bumpkin.disk.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bumpkin.disk.entities.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/12 10:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("virtual_address")
public class VirtualAddress extends BaseEntity {

    private String id;

    private String uuid;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 父级路径
     */
    private String parentPath;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 是否为文件夹 1为是 0为否
     */
    private Integer isDir;

    private Date createTime;

    private Date updateTime;
}
