package com.founder.console.web.controller;


import com.founder.console.web.bind.annotation.CurrentUser;
import com.founder.console.web.config.annotation.WebController;
import com.founder.contract.business.ClientUserService;
import com.founder.domain.business.ClientUser;
import com.founder.shiro.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@WebController
@RequestMapping
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IndexController {

    private final ClientUserService clientUserService;

    @RequestMapping(value ="/", method = RequestMethod.GET)
    public String index(@CurrentUser ClientUser clientUser) {
        if (!clientUser.isInitialized())
            return "completeInformation";
        return "index";
    }

    @RequestMapping(value = "/completeInformation", method = RequestMethod.POST)
    public String completeInformation(HttpServletRequest req, @CurrentUser ClientUser clientUser, @RequestParam(value = "password") String password, @RequestParam(value = "confirmedPassword") String confirmedPassword) {
        if (StringUtils.equals(password, confirmedPassword)) {
            clientUserService.changeInitializedPassword(clientUser.getId(), password);
            req.getSession().removeAttribute(Constants.CURRENT_USER);
            return "redirect:/";
        }
        req.setAttribute("error", "两次输入的密码不一致");
        return "/";
    }
}
