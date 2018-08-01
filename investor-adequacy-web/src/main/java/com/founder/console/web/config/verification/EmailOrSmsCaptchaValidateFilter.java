package com.founder.console.web.config.verification;

import com.founder.contract.business.VerificationCodeService;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class EmailOrSmsCaptchaValidateFilter extends AccessControlFilter {

    @Autowired
    private VerificationCodeService verificationCodeService;

    private String captchaParam = "mobileNoOrEmail";

    private String codeParam = "verifyCode";

    private String failureKeyAttribute = "error";

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);

        if (!"post".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return true;
        }

        if (request.getAttribute(failureKeyAttribute) != null) return false;

        return verificationCodeService.verify(httpServletRequest.getParameter(captchaParam), httpServletRequest.getParameter(codeParam));
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (request.getAttribute(failureKeyAttribute)== null)
            request.setAttribute(failureKeyAttribute, "验证码错误");

        return true;
    }

}
