package com.bumpkin.disk.exceptions;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 15:50
 */
public interface BaseError {
    /**
     * 获取错误码

     * @return 错误码
     */
    int getErrorCode();

    /**
     * 获取错误信息

     * @return 错误信息
     */
    String getErrorMessage();
}
