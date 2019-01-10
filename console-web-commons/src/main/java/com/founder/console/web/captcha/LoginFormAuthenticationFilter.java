package com.founder.console.web.captcha;

import com.founder.console.web.utils.WebUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginFormAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if(request.getAttribute(getFailureKeyAttribute()) != null) {
            return true;
        }
        if (isLoginRequest(request, response)) {
            return super.onAccessDenied(request, response);
        } else {
            if (WebUtils.isAjaxRequest((HttpServletRequest) request)) {
                HttpServletResponse httpServletResponse = org.apache.shiro.web.util.WebUtils.toHttp(response);
                httpServletResponse.addHeader("REQUIRE_AUTH", "true");
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            } else {
                saveRequestAndRedirectToLogin(request, response);
            }
            return false;
        }
    }


}
