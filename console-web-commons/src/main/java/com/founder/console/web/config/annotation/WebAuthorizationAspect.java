package com.founder.console.web.config.annotation;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

//@Aspect
//@Component
@Slf4j
public class WebAuthorizationAspect {

    @Before("@target(AjaxController) && @annotation(requiresRoles)")
    public void assertAuthorized(JoinPoint jp, RequiresPermissions requiresRoles) {
        for (String s : requiresRoles.value()) {
            SecurityUtils.getSubject().checkPermission(s);
        }

    }

    @Pointcut("execution(public * com.founder..*.*(..))")
    public void webLog(){}
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容

        // 记录下请求内容
        log.info("URL : " + "测试："+joinPoint);

    }



}