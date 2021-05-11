package com.bumpkin.disk.exceptions;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/07 15:53
 */
public enum  DefaultError implements BaseError {

    /**
     * 系统内部错误
     */
    SYSTEM_INTERNAL_ERROR(500, "系统内部错误"),
    /**
     * 无效参数
     */
    INVALID_PARAMETER(1, "无效的参数"),
    /**
     * 服务不存在
     */
    SERVICE_NOT_FOUND(2, "服务不存在"),
    /**
     * 参数不全
     */
    PARAMETER_REQUIRED(3, "参数不全"),
    /**
     * 参数过长
     */
    PARAMETER_MAX_LENGTH(4, "参数过长"),
    /**
     * 参数过短
     */
    PARAMETER_MIN_LENGTH(5, "参数过短"),
    /**
     * 参数出错
     */
    PARAMETER_ANNOTATION_NOT_MATCH(6, "参数出错"),
    /**
     * 参数验证失败
     */
    PARAMETER_NOT_MATCH_RULE(7, "参数验证失败"),
    /**
     * 请求方法出错
     */
    METHOD_NOT_SUPPORTED(8, "请求方法出错"),
    /**
     * 不支持的content类型
     */
    CONTENT_TYPE_NOT_SUPPORT(9, "不支持的content类型"),
    /**
     * json格式化出错
     */
    JSON_FORMAT_ERROR(10, "json格式化出错"),
    /**
     * 远程调用出错
     */
    CALL_REMOTE_ERROR(11, "远程调用出错"),
    /**
     * 服务运行SQLException异常
     */
    SQL_EXCEPTION(12, "服务运行SQLException异常"),
    /**
     * 客户端异常 给调用者 app,移动端调用
     */
    CLIENT_EXCEPTION(13, "客户端异常"),
    /**
     * 服务端异常, 微服务服务端产生的异常
     */
    SERVER_EXCEPTION(14, "服务端异常"),
    /**
     * 授权失败 禁止访问
     */
    ACCESS_DENIED(15, "授权失败 禁止访问"),

    /**
     * 授权失败 禁止访问
     */
    INVALID_TOKEN(16, "无效的token"),

    /**
     * 授权失败 禁止访问
     */
    REQUEST_TOKEN(17, "请求无token"),


    INVALID_SESSION(201, "登录已超时，请重新登入"),


    /**
     * spring dao层异常
     */
    DATA_ACCESS_EXCEPTION(18, "服务器运行DataAccessException异常"),
    ;

    int errorCode;
    String errorMessage;

    DefaultError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return 0;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
