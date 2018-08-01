package com.founder.console.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class LoginUtils {

    public static String loginDirectly(HttpServletRequest req, String userName, String password, String errorAttributeName, String loginPage, String mainPage) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
        token.setRememberMe(false);
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            log.info("登陆失败", e);
            req.setAttribute(errorAttributeName, e.getClass().getName());
            req.setAttribute("error", "用户名或密码错误");
            return loginPage;
        }
        preventSessionFixation(subject, token);
        return mainPage;
    }

    private static void preventSessionFixation(Subject subject, UsernamePasswordToken token) {
        Session session = subject.getSession();
        Map attributes = new LinkedHashMap();
        Collection<Object> keys = session.getAttributeKeys();
        for (Object key : keys) {
            Object value = session.getAttribute(key);
            if (value != null) {
                attributes.put(key, value);
            }
        }
        session.stop();
        subject.login(token);
        session = subject.getSession();
        for (Object key : attributes.keySet()) {
            session.setAttribute(key, attributes.get(key));
        }
    }
}
