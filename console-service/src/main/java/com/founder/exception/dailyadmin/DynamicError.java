package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

/**
 *
 */
public enum DynamicError implements ErrorMessage {

    DynamicRecordNotExists("700001", "未查到填报记录。" );



    private String errorMessage;

    private String errorCode;


    DynamicError(String errorCode, String errorMessage) {
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
