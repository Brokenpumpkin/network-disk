package com.bumpkin.disk.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/09 10:54
 */
@Data
@TableName("file")
public class File {

    private Integer id;

    private String fileId;

    private String originalName;

    private Integer fileSize;

    private Integer fileType;

    private String fileLocation;

    private String fileMd5;

    private Date createTime;
}
