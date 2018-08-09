package com.founder.console.web.filter;

import com.founder.shiro.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

public abstract class AbstractUserFilter extends PathMatchingFilter {

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        String username = (String) SecurityUtils.getSubject().getPrincipal();
        HttpServletRequest req = (HttpServletRequest) request;

        if (!StringUtils.isEmpty(username)) {
            Object user = getCurrentSessionUser(req);
            putCurrentUser2Request(request, username, user);
        }

        return true;
    }

    private void putCurrentUser2Request(ServletRequest request, String username, Object user) {
        request.setAttribute(Constants.CURRENT_USER, Objects.nonNull(user) ? user : getCurrentUser(username));
    }

    private Object getCurrentSessionUser(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return session.getAttribute(Constants.CURRENT_USER);
    }

    abstract Object getCurrentUser(String username);
}
