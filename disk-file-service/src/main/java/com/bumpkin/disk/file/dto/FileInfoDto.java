package com.bumpkin.disk.file.dto;

import lombok.Data;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/05/11 21:07
 */
@Data
public class FileInfoDto {
    /**
     * 附件编号
     */
    private String id;

    /**
     * 附件类型
     */
    private String fileType;

    /**
     * 附件名称
     */
    private String name;

    /**
     * 附件总大小
     */
    private Integer size;

    /**
     * 附件地址
     */
    private String relativePath;

    /**
     * 附件MD5标识
     */
    private String uniqueIdentifier;

    private String path;

    /**
     * 附件所属项目ID
     */
    private String refProjectId;
}
