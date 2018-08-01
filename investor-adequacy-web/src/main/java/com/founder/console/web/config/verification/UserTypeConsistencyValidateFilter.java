package com.founder.console.web.config.verification;

import com.founder.contract.business.ClientUserService;
import com.founder.domain.business.ClientUser;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class UserTypeConsistencyValidateFilter extends AccessControlFilter {

    @Autowired
    private ClientUserService clientUserService;

    private String failureKeyAttribute = "error";

    private String loginNameParam = "username";

    private String personalParam = "personal";

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);

        if (!"post".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return true;
        }


        if (request.getAttribute(failureKeyAttribute) != null) return false;

        String loginName = httpServletRequest.getParameter(loginNameParam);

        ClientUser clientUser = clientUserService.findClientUserWithNoException(loginName);
        if (Objects.isNull(clientUser)) {
            return true;
        }
        String personal = httpServletRequest.getParameter(personalParam);

        return clientUser.isPersonalConsistency(Boolean.valueOf(personal));
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        if (request.getAttribute(failureKeyAttribute) == null) {
            HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
            boolean personal = Boolean.valueOf(httpServletRequest.getParameter(personalParam));
            if (personal)
                request.setAttribute(failureKeyAttribute, "请选择机构登录");
            else
                request.setAttribute(failureKeyAttribute, "请选择自然人登录");
        }

        return true;
    }
}
