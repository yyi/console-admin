package com.founder.console.web.filter;

import com.founder.contract.sysadmin.UserService;
import com.founder.domain.sysadmin.User;
import com.founder.shiro.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;


public class SysUserFilter extends AbstractUserFilter {

    @Autowired
    private UserService userService;

    @Override
    Object getCurrentUser(String username) {
        return userService.findByUsername(username);
    }

}
