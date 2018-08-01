package com.founder.console.web.controller;

import com.founder.console.web.config.annotation.WebController;
import com.founder.console.web.utils.LoginUtils;
import com.founder.contract.business.ClientUserService;
import com.octo.captcha.CaptchaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@WebController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoginController {

    private final ClientUserService clientUserService;

    private final static String errorAttributeName = "shiroLoginFailure";

    private final static String errorAttribute = "error";

    private String loginPage = "login";

    private String mainPage = "redirect:/";

    @Value("${console.security.password.prefix:false}")
    protected boolean prefixPasswordFlag;

    @Value("${console.security.password.initial:fz601901}")
    protected String initialPassword;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginForm(HttpServletRequest req, Model model, @RequestParam(value = errorAttributeName, required = false) String errorMsg, HttpSession session) {
        if (isAuthenticated()) return mainPage;


        putErrorMessage(req, model, errorMsg);
        model.addAttribute("personalFlag", true);

        return loginPage;
    }

    private void putErrorMessage(HttpServletRequest req, Model model, String errorMsg) {

        String exceptionClassName = (String) req.getAttribute(errorAttributeName);
        String error;

        if (CaptchaException.class.getName().equals(exceptionClassName)) {
            error = "校验码错误";
        } else if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
            error = "用户名或密码错误"; //账户不存在
        } else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            error = "用户名或密码错误";
        } else if (exceptionClassName != null) {
            error = "用户名或密码错";
        } else {
            error = errorMsg;
        }
        if (StringUtils.isNotBlank(error))
            model.addAttribute("error", error);
    }

    private boolean isAuthenticated() {
        return SecurityUtils.getSubject().isAuthenticated();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest req, Model model, @RequestParam(value = "username") String userName, @RequestParam(value = "password") String password, @RequestParam(value = "personal") boolean personal) {
        model.addAttribute("personalFlag", personal);
        if (isAuthenticated()) return mainPage;

        if (req.getAttribute(errorAttributeName) != null || req.getAttribute(errorAttribute) != null) {
            putErrorMessage(req, model, (String) req.getAttribute(errorAttribute));
            return loginPage;
        }

        if (!Objects.isNull(clientUserService.findClientUserWithNoException(userName))) {
            return loginDirectly(req, userName, password);
        } else {
            String initPassword = prefixPasswordFlag ? userName.substring(0, 6) : initialPassword;
            if(!Objects.equals(password,initPassword)){
                req.setAttribute(errorAttributeName,IncorrectCredentialsException.class.getName());
                putErrorMessage(req, model, "");
                return  loginPage;
            }
            clientUserService.createUserByDefaultProfile(userName, initPassword, personal);
            return loginDirectly(req, userName, initPassword);
        }

    }

    private String loginDirectly(HttpServletRequest req, String userName, String password) {
        return LoginUtils.loginDirectly(req, userName, password, errorAttributeName, loginPage, mainPage);
    }
}
