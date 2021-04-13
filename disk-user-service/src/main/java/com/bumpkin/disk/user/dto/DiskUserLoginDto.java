package com.bumpkin.disk.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/13 12:19
 */
@ApiModel(value = "DiskUserLoginDto", description = "用户登录")
@Data
public class DiskUserLoginDto {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "手机号码")
    private String phoneNum;

    @ApiModelProperty(value = "密码")
    private String password;
}
