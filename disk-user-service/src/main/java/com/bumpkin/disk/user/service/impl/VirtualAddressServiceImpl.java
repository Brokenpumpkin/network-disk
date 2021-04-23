package com.bumpkin.disk.user.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bumpkin.disk.entities.BaseEntity;
import com.bumpkin.disk.entities.DiskUser;
import com.bumpkin.disk.user.dao.VirtualAddressMapper;
import com.bumpkin.disk.user.entity.VirtualAddress;
import com.bumpkin.disk.user.service.VirtualAddressService;
import com.bumpkin.disk.utils.EntityUtil;
import org.springframework.stereotype.Service;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/22 20:57
 */
@Service
public class VirtualAddressServiceImpl extends ServiceImpl<VirtualAddressMapper, VirtualAddress> implements VirtualAddressService {
    @Override
    public Boolean addUserRootDir(DiskUser diskUser) {
        VirtualAddress virtualAddress = new VirtualAddress();
        String virtualAddressUuid = IdUtil.simpleUUID();
        virtualAddress.setId(virtualAddressUuid);
        virtualAddress.setUuid(virtualAddressUuid);
        String dirId = IdUtil.simpleUUID();
        virtualAddress.setFileId(dirId);
        virtualAddress.setUserId(diskUser.getUserId());
        virtualAddress.setUserFileName(diskUser.getUsername());
        virtualAddress.setFileSize(0);
        virtualAddress.setIsDir(1);
        virtualAddress.setIsRoot(1);
        //父级路径只写上一级文件夹
        virtualAddress.setParentPath(null);
        virtualAddress.setFullParentPath(null);
        BaseEntity newEntity = EntityUtil.getNewEntity();
        virtualAddress.setCreateTime(newEntity.getCreateTime());
        virtualAddress.setUpdateTime(newEntity.getUpdateTime());
        return this.baseMapper.insert(virtualAddress) == 1;
    }
}
