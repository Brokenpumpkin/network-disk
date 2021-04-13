package com.bumpkin.disk.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 14:26
 */
@ApiModel(value = "DiskUserRegisterDto", description = "用户注册")
@Data
public class DiskUserRegisterDto {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "用户等级")
    private String level;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "别名")
    private String alias;
}
