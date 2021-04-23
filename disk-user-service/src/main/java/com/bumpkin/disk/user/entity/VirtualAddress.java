package com.bumpkin.disk.user.entity;

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
     * 用户显示的文件名称
     */
    private String userFileName;

    /**
     * 上一级父级路径
     */
    private String parentPath;

    /**
     * 全父级路径
     */
    private String fullParentPath;
    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 是否为文件夹 1为是 0为否
     */
    private Integer isDir;

    /**
     * 是否为用户根目录 1是 0否
     */
    private Integer isRoot;

    private Date createTime;

    private Date updateTime;
}
