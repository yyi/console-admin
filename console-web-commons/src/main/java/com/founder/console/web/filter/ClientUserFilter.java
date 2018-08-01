package com.founder.console.web.filter;

import com.founder.contract.business.ClientUserService;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientUserFilter extends AbstractUserFilter {

    @Autowired
    private ClientUserService clientUserService;

    @Override
    Object getCurrentUser(String username) {
        return clientUserService.findClientUser(username);
    }
}
