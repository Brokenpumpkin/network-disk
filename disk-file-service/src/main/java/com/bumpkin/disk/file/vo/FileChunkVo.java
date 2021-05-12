package com.bumpkin.disk.file.vo;

import lombok.Data;

import java.util.ArrayList;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/05/11 20:39
 */
@Data
public class FileChunkVo {
    /**
     * 是否跳过上传（已上传的可以直接跳过，达到秒传的效果）
     */
    private boolean skipUpload;

    /**
     * 已经上传的文件块编号，可以跳过，断点续传
     */
    private ArrayList<Integer> uploadedChunks;

    /**
     * 已上传完整附件的地址
     */
    private String location;
}
