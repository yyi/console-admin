package com.founder.console.web;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;


@SpringBootApplication(exclude = DispatcherServletAutoConfiguration.class)
@ComponentScan(
        basePackages = "com",
        excludeFilters =
        @ComponentScan.Filter({Controller.class, ControllerAdvice.class}))
@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@EnableScheduling
@EnableRedisHttpSession
public class ConsoleWebApplication   {

    public static void main(String[] args) {
        SpringApplication.run(ConsoleWebApplication.class, args);
    }



//    @Override
//    public void onStartup(ServletContext container) throws ServletException {
//
//        AnnotationConfigWebApplicationContext webContext =
//                new AnnotationConfigWebApplicationContext();
//        webContext.register(WebConfig.class);
//        ServletRegistration.Dynamic dispatcher = container.addServlet(
//                "springWebDispatcher", new DispatcherServlet(webContext)
//        );
//        dispatcher.setLoadOnStartup(1);
//        dispatcher.setMultipartConfig(new MultipartConfigElement(
//                null, 20_971_520L, 41_943_040L, 512_000
//        ));
//        dispatcher.addMapping("/");
//
//        AnnotationConfigWebApplicationContext restContext =
//                new AnnotationConfigWebApplicationContext();
//        restContext.register(AjaxConfig.class);
//        DispatcherServlet servlet = new DispatcherServlet(restContext);
//        servlet.setDispatchOptionsRequest(true);
//        dispatcher = container.addServlet(
//                "springRestDispatcher", servlet
//        );
//        dispatcher.setLoadOnStartup(2);
//        dispatcher.addMapping("/rest/*");
//
//    }



}
