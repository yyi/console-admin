package com.founder.console.web.controller.business;

import com.founder.console.web.bind.annotation.CurrentUser;
import com.founder.console.web.config.annotation.WebController;
import com.founder.contract.sysadmin.DictionaryService;
import com.founder.domain.sysadmin.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@WebController
@RequestMapping
public class BusiController {

    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping("/clientUser")
    public String clientUserIndex() {
        return "busi/clientUser";
    }




}
