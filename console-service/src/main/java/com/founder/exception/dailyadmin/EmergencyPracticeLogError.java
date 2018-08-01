package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

public enum EmergencyPracticeLogError implements ErrorMessage {

    EmergencyPracticeLogNotEx("510000", "应急演练不存在"),

    ;

    private String errorMessage;

    private String errorCode;

    EmergencyPracticeLogError(String errorCode, String errorMessage) {
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
