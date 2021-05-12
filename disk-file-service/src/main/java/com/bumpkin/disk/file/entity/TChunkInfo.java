package com.bumpkin.disk.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/05/11 19:14
 */
@Data
@TableName(value = "t_chunk_info")
public class TChunkInfo implements Serializable {
    private static final long serialVersionUID = 1L;


    private String id;
    /**
     * 当前文件块，从1开始
     */
    private Integer chunkNumber;
    /**
     * 每块大小
     */
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    private Long currentChunkSize;
    /**
     * 总大小
     */
    @TableField(exist = false)
    private Long totalSize;
    /**
     * 文件标识
     */
    private String identifier;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * 总块数
     */
    private Integer totalChunks;
    /**
     * 文件类型
     */
    private String type;

    /**
     * 块内容
     */
    @TableField(exist = false)
    private transient MultipartFile upfile;
}
