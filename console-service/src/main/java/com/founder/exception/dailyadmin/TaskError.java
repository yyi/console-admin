package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

/**
 * Created by 许旭 on 2017/7/26.
 */
public enum TaskError implements ErrorMessage {

    TaskNotExists("220000", "任务不存在。" ),
    TaskTemplateNotExists("220001", "任务关联模板未配置，不能启动任务。" ),
    TaskOrganizationNotExists("220002", "任务未分配部门，不能启动任务。" ),
    TaskIsAvailiable("220003", "任务已启动，无法操作。" ),
    TaskExecutionNotExists("220004","任务不存在"),
    TaskExecutionStatusError("220006","任务已完成"),
    TaskExecutionStatusNotError("220009","任务未完成"),
    TaskHasExistsOrganization("220007","任务已关联部门,请先删除关联的部门"),
    TaskHasExistsTemplate("220008","任务已关联模板,请先删除关联的模板");
    private String errorMessage;

    private String errorCode;


    TaskError(String errorCode, String errorMessage) {
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
