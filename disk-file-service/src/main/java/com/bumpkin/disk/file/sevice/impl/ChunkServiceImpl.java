package com.bumpkin.disk.file.sevice.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.file.dao.TChunkInfoMapper;
import com.bumpkin.disk.file.entity.TChunkInfo;
import com.bumpkin.disk.file.sevice.ChunkService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/05/11 19:22
 */
@Service
public class ChunkServiceImpl extends ServiceImpl<TChunkInfoMapper, TChunkInfo> implements ChunkService {

    @Override
    public int saveChunk(TChunkInfo chunk) {
        chunk.setId(IdUtil.simpleUUID());
        this.baseMapper.insert(chunk);
        return 0;
    }

    @Override
    public boolean checkChunk(String identifier, Integer chunkNumber) {
        return false;
    }

    @Override
    public ArrayList<Integer> checkChunk(TChunkInfo chunk) {
        QueryWrapper<TChunkInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("identifier", chunk.getIdentifier());
        queryWrapper.eq("filename", chunk.getFilename());
        List<TChunkInfo> tChunkInfos = this.baseMapper.selectList(queryWrapper);
        ArrayList<Integer> list = new ArrayList<>();
        for (TChunkInfo t: tChunkInfos) {
            list.add(t.getChunkNumber());
        }
        return list;
    }
}
