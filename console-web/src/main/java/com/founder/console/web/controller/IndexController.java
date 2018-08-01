package com.founder.console.web.controller;

import com.founder.console.web.bind.annotation.CurrentUser;

import com.founder.console.web.config.annotation.WebController;
import com.founder.contract.sysadmin.ResourceService;
import com.founder.domain.sysadmin.User;
import com.founder.form.sysadmin.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;


@WebController
@RequestMapping
public class IndexController {

    @Autowired
    private ResourceService resourceService;

    @RequestMapping("/")
    public String index(@CurrentUser User loginUser, Model model) {
        List<Long> roleIds = loginUser.getRoles().stream().map(role -> role.getId()).collect(Collectors.toList());

        List<Menu> menus = resourceService.getUserResourceByRoleIds(roleIds);

        model.addAttribute("menus", menus);

        model.addAttribute("changPasswordIndexUrl", "/changPasswordIndex");
        return "index";
    }

    @RequestMapping("/index_main")
    public String main() {
        return "index_main";
    }

}
