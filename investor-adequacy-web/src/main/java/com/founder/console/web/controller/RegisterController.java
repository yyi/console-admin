package com.founder.console.web.controller;

import com.founder.console.web.config.annotation.WebController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

@WebController
@Slf4j
public class RegisterController {

    @RequestMapping(value = "/register")
    public  String showRegisterForm(){
        return "register";
    }

}
