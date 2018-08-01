package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

/**
 *
 */
public enum SystemLogError implements ErrorMessage {

    SystemRuntimeLogNotExists("400001", "系统运行日志不存在。" ),

    CurrentUserHasNoPermission("400002","当前用户没有权限查看这个部门"),

    SystemLogStatValuesNotExists("400003","系统运行日志统计数据不存在"),

    ;



    private String errorMessage;

    private String errorCode;


    SystemLogError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }
}
