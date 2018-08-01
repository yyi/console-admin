package com.founder.exception.sysadmin;

import com.founder.Exception.ErrorMessage;
import lombok.ToString;

@ToString
public enum SysadminError implements ErrorMessage {
    LackOfPermission("999999", "无权限"),
    UserNotExists("100000", "用户不存在"),
    ResourceNotExists("100001", "资源不存在"),
    ResourceHasSubResource("100002", "资源存在子资源"),
    RolesNotExists("100003", "角色不存在"),
    RoleHasAssociatedUser("100004", "角色存在关联用户"),
    OrganizationNotExists("100005", "部门不存在"),
    OrganizationHasAffiliatedOrganization("100006", "存在下级机构"),
    OrganizationHasUsers("100007", "部门下存在员工"),
    ResourceHasSAssociatedRole("100008", "资源有关联角色" ),
    LoginNameExists("100009", "登录名已存在" ),
    organizationHasInfiniteRecursion("100010", "部门数据存在死循环,请通知管理员检查部门数据配置" ),
    RoleNameExists("100011", "角色名已存在" ),
    OrganizationNoExists("100012", "部门编号已存在"),

    PdfFileGenerateError("100013", "PDF文件生成失败"),
    ;

    private String errorMessage;

    private String errorCode;

    SysadminError(String errorCode, String errorMessage) {
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
