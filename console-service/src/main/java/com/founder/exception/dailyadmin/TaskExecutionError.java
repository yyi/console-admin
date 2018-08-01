package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

/**
 *
 */
public enum TaskExecutionError implements ErrorMessage {

    TaskExecutionNotExists("500001", "任务执行情况不存在。" ),

    TaskExecutionIsExecuted("500002", "任务已经执行，不能再操作。" ),

    TaskExecutionStatExcelError("500003", "任务填表统计,生成导出文件失败。" ),

    TaskExecutionOverTimeNotUpdate("500004", "该条记录已超过时限,无法进行修改" ),;

    private String errorMessage;

    private String errorCode;


    TaskExecutionError(String errorCode, String errorMessage) {
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
