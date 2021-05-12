package com.bumpkin.disk.file.sevice;

import com.bumpkin.disk.file.entity.TChunkInfo;

import java.util.ArrayList;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/05/11 19:21
 */
public interface ChunkService {
    /**
     * 保存文件块
     *
     * @param chunk
     */
    public int saveChunk(TChunkInfo chunk);

    /**
     * 检查文件块是否存在
     *
     * @param identifier
     * @param chunkNumber
     * @return
     */
    boolean checkChunk(String identifier, Integer chunkNumber);

    /**
     * 查询哪些文件块已经上传
     * @param chunk
     * @return
     */
    ArrayList<Integer> checkChunk(TChunkInfo chunk);
}
