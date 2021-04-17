package com.bumpkin.disk.file.sevice.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.entities.BaseEntity;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.file.dao.VirtualAddressMapper;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.VirtualAddress;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.utils.EntityUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/14 14:32
 */
@Service
public class VirtualAddressServiceImpl extends ServiceImpl<VirtualAddressMapper, VirtualAddress> implements VirtualAddressService {

    @Override
    public void addFile(DiskFile diskFile, String userId, String parentPath) {
        //虚拟地址
        VirtualAddress virtualAddress = new VirtualAddress();
        String virtualAddressUuid = IdUtil.simpleUUID();
        virtualAddress.setId(virtualAddressUuid);
        virtualAddress.setUuid(virtualAddressUuid);
        virtualAddress.setFileId(diskFile.getFileId());
        virtualAddress.setUserId(userId);
        virtualAddress.setFileName(diskFile.getOriginalName());
        virtualAddress.setFileSize(diskFile.getFileSize());
        virtualAddress.setIsDir(0);
        virtualAddress.setIsParent(0);
        // todo 父级路径只写上一级文件夹
        virtualAddress.setParentPath(parentPath);
        BaseEntity newEntity = EntityUtil.getNewEntity();
        virtualAddress.setCreateTime(newEntity.getCreateTime());
        virtualAddress.setUpdateTime(newEntity.getUpdateTime());
        this.baseMapper.insert(virtualAddress);
    }

    @Override
    public Boolean addDir(String dirName, String parentPath, DiskUser diskUser) {
        VirtualAddress virtualAddress = new VirtualAddress();
        String virtualAddressUuid = IdUtil.simpleUUID();
        virtualAddress.setId(virtualAddressUuid);
        virtualAddress.setUuid(virtualAddressUuid);
        String dirId = IdUtil.simpleUUID();
        virtualAddress.setFileId(dirId);
        virtualAddress.setUserId(diskUser.getUserId());
        virtualAddress.setFileName(dirName);
        virtualAddress.setFileSize(0);
        virtualAddress.setIsDir(1);
        virtualAddress.setIsParent(0);
        // todo 父级路径只写上一级文件夹
        virtualAddress.setParentPath(parentPath);
        BaseEntity newEntity = EntityUtil.getNewEntity();
        virtualAddress.setCreateTime(newEntity.getCreateTime());
        virtualAddress.setUpdateTime(newEntity.getUpdateTime());
        return this.baseMapper.insert(virtualAddress) == 1;
    }

    @Override
    public Boolean fileDirVirtualAddressMove(String fileName, String oldPath, String newPath, DiskUser diskUser) {
        String oldParentPath = StrUtil.subBefore(oldPath, "/", true);
        String newParentPath = StrUtil.subBefore(newPath, "/", true);
        VirtualAddress virtualAddressFile = this.getDiskFileByFileNameAndParentPathAndUserId(fileName, oldParentPath, diskUser.getUserId());
        if (virtualAddressFile != null) {
            virtualAddressFile.setParentPath(newParentPath);
            return true;
        }
        return false;
    }

    @Override
    public List<VirtualAddress> getFileByUserAndParentPath(DiskUser diskUser, String parentPath) {
        QueryWrapper<VirtualAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", diskUser.getUserId());
        queryWrapper.eq("parent_path", parentPath);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public VirtualAddress getDiskFileByFileNameAndParentPathAndUserId(String fileName, String parentPath, String userId) {
        QueryWrapper<VirtualAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("parent_path", parentPath);
        return this.baseMapper.selectOne(queryWrapper);
    }
}
