package com.founder.console.web.controller;

import com.founder.console.web.config.annotation.WebController;
import com.founder.shiro.token.OaUserToken;
import com.octo.captcha.CaptchaException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@WebController
@Slf4j
public class LoginController {

    private final static String errorAttributeName = "shiroLoginFailure";

    @RequestMapping(value = "/login")
    public String showLoginForm(HttpServletRequest req, Model model, @RequestParam(value = errorAttributeName, required = false) String errorMsg) {
        req.getSession();

        if (isAuthenticated()) return "redirect:/";

        String exceptionClassName = (String) req.getAttribute(errorAttributeName);
        String error = null;

        if (CaptchaException.class.getName().equals(exceptionClassName)) {
            error = "验证码错误";
        } else if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
            error = "用户名/密码错误"; //账户不存在
        } else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            error = "用户名/密码错误";
        } else if (ExcessiveAttemptsException.class.getName().equals(exceptionClassName)) {
            error = "密码错误次数已达上限（5次），请稍后再试";
        } else if (exceptionClassName != null) {
            error = "用户名/密码错";
        } else {
            error = errorMsg;
        }
        model.addAttribute("error", error);

        return "login";
    }

    @RequestMapping(value = "/oaLogin")
    public String oaLogin(@RequestParam("name") String name,
                          @RequestParam(value = "timeStamp", required = false) String timeStamp,
                          @RequestParam(value = "mac", required = false) String mac, RedirectAttributes redirectAttributes) {

        OaUserToken oaUserToken = new OaUserToken(name, timeStamp);
        if (Objects.nonNull(mac))
            oaUserToken.setPassword(mac.toCharArray());
        try {
            SecurityUtils.getSubject().login(oaUserToken);
        } catch (AuthenticationException e) {
            log.error("oa用户登陆失败", e);
            redirectAttributes.addAttribute(errorAttributeName, e.getMessage());
            return "redirect:/login";
        }
        return "redirect:/";
    }

    private boolean isAuthenticated() {
        return SecurityUtils.getSubject().isAuthenticated();
    }


}
