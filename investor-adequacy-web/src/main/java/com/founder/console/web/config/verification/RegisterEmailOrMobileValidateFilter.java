package com.founder.console.web.config.verification;

import com.founder.contract.business.ClientUserService;
import com.founder.service.VerificationUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RegisterEmailOrMobileValidateFilter extends AccessControlFilter {


    @Autowired
    private ClientUserService clientUserService;

    private String loginNameParam = "userName";

    private String emailOrMobileNoParam = "mobileNoOrEmail";

    private String failureKeyAttribute = "error";

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);

        if (!"post".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return true;
        }
        if (request.getAttribute(failureKeyAttribute) != null) return false;

        String loginName = httpServletRequest.getParameter(loginNameParam);
        String mobileOrEmail = httpServletRequest.getParameter(emailOrMobileNoParam);
        if (!VerificationUtils.isEmailOrMobileNo(mobileOrEmail)) return false;
        if (VerificationUtils.isEmail(mobileOrEmail))
            return clientUserService.verifyEmail(loginName, mobileOrEmail);
        else
            return clientUserService.verifyMobileNo(loginName, mobileOrEmail);

    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (request.getAttribute(failureKeyAttribute) == null)
            request.setAttribute(failureKeyAttribute, "输入的手机号/邮箱与注册信息不一致");
        return true;
    }
}
