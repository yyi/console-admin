package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

/**
 *
 */
public enum TestResponseLogError implements ErrorMessage {

    TestResponseLogNotExists("600001", "测试反馈记录不存在。" );



    private String errorMessage;

    private String errorCode;


    TestResponseLogError(String errorCode, String errorMessage) {
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
