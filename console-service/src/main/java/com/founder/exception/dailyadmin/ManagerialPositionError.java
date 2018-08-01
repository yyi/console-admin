package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

public enum ManagerialPositionError implements ErrorMessage {

    ManagerialPositionNotExists("910000", "用户不存在" ),
    LoginNameNotExists("910001", "登录名不存在"),
    LoginNameHasRepeat("910002", "已存在岗位,不能增加"),
    OaNameHasRepeat("910003", "OaName已存在"),
    ;

    private String errorMessage;

    private String errorCode;

    ManagerialPositionError(String errorCode, String errorMessage) {
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
