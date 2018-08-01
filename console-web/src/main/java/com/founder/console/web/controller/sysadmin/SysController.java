package com.founder.console.web.controller.sysadmin;

import com.founder.console.web.bind.annotation.CurrentUser;
import com.founder.console.web.config.annotation.WebController;
import com.founder.contract.sysadmin.DictionaryService;
import com.founder.domain.sysadmin.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@WebController
@RequestMapping
public class SysController {

    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping("/user")
    public String userIndex() {
        return "sys/user";
    }

    @RequestMapping("/changPasswordIndex")
    public String changePasswordIndex(Model model,@CurrentUser User user) {

        model.addAttribute("userId", user.getId());
        return "sys/changepwd";
    }

    @RequestMapping("/resource")
    public String resourceIndex() {
        return "sys/resource";
    }

    @RequestMapping("/role")
    public String roleIndex() {
        return "sys/role";
    }

    @RequestMapping("/organization")
    public String organizationIndex(Model model) {
        model.addAttribute("data", dictionaryService.getDictionaryMapsByTypes("ORGANIZATION_LEVEL","ORGANIZATION_TYPE"));
        return "sys/organization";
    }

}
