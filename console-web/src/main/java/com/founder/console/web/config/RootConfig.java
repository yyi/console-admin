package com.founder.console.web.config;

import com.founder.console.web.ConsoleWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class RootConfig extends AbstractRootConfig{

    @Override
    protected Class getBootClass() {
        return ConsoleWebApplication.class;
    }


}
