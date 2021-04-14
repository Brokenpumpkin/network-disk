package com.bumpkin.disk.file.sevice.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.entities.BaseEntity;
import com.bumpkin.disk.file.dao.VirtualAddressMapper;
import com.bumpkin.disk.file.entity.DiskFile;
import com.bumpkin.disk.file.entity.VirtualAddress;
import com.bumpkin.disk.file.sevice.VirtualAddressService;
import com.bumpkin.disk.utils.EntityUtil;
import org.springframework.stereotype.Service;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/14 14:32
 */
@Service
public class VirtualAddressServiceImpl extends ServiceImpl<VirtualAddressMapper, VirtualAddress> implements VirtualAddressService {

    @Override
    public void add(DiskFile diskFile, String userId, String parentPath) {
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
        virtualAddress.setParentPath(parentPath);
        BaseEntity newEntity = EntityUtil.getNewEntity();
        virtualAddress.setCreateTime(newEntity.getCreateTime());
        virtualAddress.setUpdateTime(newEntity.getUpdateTime());
        this.baseMapper.insert(virtualAddress);
    }
}
