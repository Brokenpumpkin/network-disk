package com.bumpkin.disk.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 15:58
 */
@Data
@TableName(value = "pan_user")
public class DiskUser {
    private String id;

    private String userId;

    private String username;

    private String password;

    private String level;

    private String email;

    private String phone;

    private String alias;
}
