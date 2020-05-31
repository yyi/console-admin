package com.founder.console.web.config;

import com.founder.console.web.captcha.GMailEngine;
import com.founder.console.web.captcha.ImageCaptchaService;
import com.founder.console.web.captcha.LoginFormAuthenticationFilter;
import com.founder.console.web.filter.SysUserFilter;
import com.founder.shiro.realm.UserRealm;
import com.founder.shiro.spring.SpringCacheManagerWrapper;
import com.octo.captcha.engine.CaptchaEngine;
//import net.sf.ehcache.CacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
public class SecurityConfig extends  AbstractSecurityConfig {

    @Bean
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(credentialsMatcher());
        userRealm.setAuthenticationCachingEnabled(authenticationCachingEnabled);
        userRealm.setAuthorizationCachingEnabled(authorizationCachingEnabled);
        return userRealm;
    }

    @Override
    void initRealms(Collection<Realm> realms) {
        realms.add(userRealm());
    }

    @Bean
    public SysUserFilter sysUserFilter() {
        SysUserFilter sysUserFilter = new SysUserFilter();
        return sysUserFilter;
    }

    @Bean
    public FormAuthenticationFilter formAuthenticationFilter() {
        LoginFormAuthenticationFilter formAuthenticationFilter = new LoginFormAuthenticationFilter();
        formAuthenticationFilter.setUsernameParam("username");
        formAuthenticationFilter.setPasswordParam("password");
        //formAuthenticationFilter.setRememberMeParam("rememberMe");
        formAuthenticationFilter.setFailureKeyAttribute("shiroLoginFailure");
        return formAuthenticationFilter;
    }

    @Override
    void initShiroFilterFactoryBean(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        shiroFilterFactoryBean.setLoginUrl("/login");
        Map<String, Filter> mp = new HashMap<>();
        mp.put("authc", formAuthenticationFilter());
        mp.put("sysUser", sysUserFilter());
        mp.put("jCaptchaValidate", jCaptchaValidateFilter());
        shiroFilterFactoryBean.setFilters(mp);
        Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
        chains.put("/jcaptcha*", "anon");
        chains.put("/**/favicon.ico*", "anon");
        chains.put("/**/*.css", "anon");
        chains.put("/**/*.js", "anon");
        chains.put("/static/**/*", "anon");
        chains.put("/spa/**/*", "anon");
        chains.put("/login", "jCaptchaValidate,authc");
        chains.put("/logout", "logout");
        chains.put("/rest/**", "authc");
        chains.put("/**", "user,sysUser");
    }

    @Override
    CaptchaEngine getCaptchaEngine()  {
        return new GMailEngine();
    }

    @Bean("GMailCaptchaService")
    @Override
    public ImageCaptchaService captchaService(SpringCacheManagerWrapper cacheManager) throws Exception {
        return super.captchaService(cacheManager);
    }


    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
