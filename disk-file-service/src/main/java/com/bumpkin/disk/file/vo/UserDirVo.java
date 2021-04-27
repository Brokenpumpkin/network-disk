package com.bumpkin.disk.file.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/26 11:16
 */
@Data
public class UserDirVo {

    private String id;

    private String name;

    private String path;

    private List<UserDirVo> children;
}
