package com.bumpkin.disk.entities;

import lombok.Data;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 15:58
 */
@Data
public class User {
    private int id;

    private String userName;

    private String passWord;

    private String levelType;

    private String email;

    private String phone;

    private String alias;
}
