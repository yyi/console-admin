package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

public enum TemplateError implements ErrorMessage {

    TemplateNotExists("210000", "模板不存在" ),
    TemplateElementNotExists("210001", "模板元素不存在" ),
    ElementNotExists("210002", "元素不存在"),
    TemplateHasAssociatedTask("210003", "模板存在关联任务" );


    private String errorMessage;

    private String errorCode;

    TemplateError(String errorCode, String errorMessage) {
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
