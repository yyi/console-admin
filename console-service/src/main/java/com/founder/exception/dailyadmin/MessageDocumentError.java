package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

public enum MessageDocumentError implements ErrorMessage {

    MessageDocumentNotEx("310000", "信息文档不存在"),

    ;

    private String errorMessage;

    private String errorCode;

    MessageDocumentError(String errorCode, String errorMessage) {
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
