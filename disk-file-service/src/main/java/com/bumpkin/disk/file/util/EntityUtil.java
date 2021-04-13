package com.bumpkin.disk.file.util;

import com.bumpkin.disk.entities.BaseEntity;

import java.util.Date;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/12 11:44
 */
public class EntityUtil {
    public static BaseEntity getNewEntity()
    {
        BaseEntity baseEntity=new BaseEntity();
        baseEntity.setCreateTime(new Date());
        baseEntity.setUpdateTime(new Date());
//        baseEntity.setOperator(ServiceUtil.getOperatorId());
//        baseEntity.setCreator(ServiceUtil.getOperatorId());
        return baseEntity;
    }
    public static BaseEntity getUpdateEntity()
    {
        BaseEntity baseEntity=new BaseEntity();
        baseEntity.setUpdateTime(new Date());
//        baseEntity.setOperator(ServiceUtil.getOperatorId());
        return baseEntity;
    }
}
