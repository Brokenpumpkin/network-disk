package com.bumpkin.disk.result;

import com.bumpkin.disk.exceptions.BaseError;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 15:43
 */
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int STATE_OK = 200;

    public static final int STATE_ERROR = 500;

    public static final String MESSAGE_SUCCESS = "请求成功";


    @ApiModelProperty(value = "响应状态码")
    private int code=STATE_OK;
    @ApiModelProperty(value = "响应信息值")
    private String message=MESSAGE_SUCCESS;
    @ApiModelProperty(value = "响应数据体")
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public static <T> ResponseResult<T> createErrorResult(T data, String message) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setData(data);
        result.setMessage(message);
        result.setCode(STATE_ERROR);
        return result;
    }


    public static <T> ResponseResult<T> createErrorResult(BaseError error) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setMessage(error.getErrorMessage());
        result.setCode(error.getErrorCode());
        return result;
    }

    public static <T> ResponseResult<T> createErrorResult(String message) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setMessage(message);
        result.setCode(STATE_ERROR);
        return result;
    }


    public static <T> ResponseResult<T> createSuccessResult() {
        ResponseResult<T> result = new ResponseResult<T>();
        return result;
    }
    public static <T> ResponseResult<T> createSuccessResult(String message) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setMessage(message);
        return result;
    }



    public static <T> ResponseResult<T> createSuccessResult(T data) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setData(data);
        return result;
    }

    public static <T> ResponseResult<T> createSuccessResult(T data, String message) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setData(data);
        result.setMessage(message);
        return result;
    }
}
