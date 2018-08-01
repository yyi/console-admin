package com.founder.exception.business;

import com.founder.Exception.ErrorMessage;
import lombok.ToString;

@ToString
public enum BusinessError implements ErrorMessage {

    MailMessageError("500000", "邮箱内容错误"),
    SmsMessageError("500001", "短息发送失败"),
    VerificationCodeNotDisabled("500002", "请勿频繁申请"),
//    VerificationCodeNotExist("500003", "无有效的验证码"),
    SmsMessageExceedDayQuota("500004", "短息发送次数超过日限制次数"),
    SmsMessageExceedTotalQuota("500005", "短息发送次数超过总限制次数"),
    RegisteredMailOrMobileError("500006", "手机号/邮箱与注册账户不一致"),
    BusinessOrderIsExit("600001", "申请业务不存在"),
    BusinessOrderOperationsIsExit("600002", "申请业务操作不存在"),
    BusinessNotInUser("600003", "该笔业务不属于该登陆用户"),
    BusinessStatusError("600004", "业务状态异常"),
    BusinessServiceTypeError("600005", "业务选择异常"),
    DownloadError("600006", "下载异常"),
    ;

    private String errorMessage;

    private String errorCode;

    BusinessError(String errorCode, String errorMessage) {
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
