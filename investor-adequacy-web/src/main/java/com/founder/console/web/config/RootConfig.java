package com.founder.console.web.config;

import com.founder.console.web.InvestorAdequacyWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@EnableAsync
public class RootConfig extends AbstractRootConfig{


    @Override
    protected Class getBootClass() {
        return InvestorAdequacyWebApplication.class;
    }

}
