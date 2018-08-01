package com.founder.console.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;


@SpringBootApplication(exclude = DispatcherServletAutoConfiguration.class)
@ComponentScan(
        basePackages = "com",
        excludeFilters =
        @ComponentScan.Filter({Controller.class, ControllerAdvice.class}))
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableRedisHttpSession
public class InvestorAdequacyWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(InvestorAdequacyWebApplication.class, args);
    }
}
