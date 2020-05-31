package com.founder.console.web.config;

import com.founder.console.web.captcha.ImageCaptchaService;
import com.founder.console.web.captcha.MixedCaptchaEngine;
import com.founder.console.web.config.verification.EmailOrSmsCaptchaValidateFilter;
import com.founder.console.web.config.verification.RegisterEmailOrMobileValidateFilter;
import com.founder.console.web.config.verification.UserTypeConsistencyValidateFilter;
import com.founder.console.web.filter.ClientUserFilter;
import com.founder.contract.sysadmin.DictionaryService;
import com.founder.shiro.realm.ClientUserRealm;
import com.founder.shiro.spring.SpringCacheManagerWrapper;
import com.founder.utils.ConsoleUtils;
import com.octo.captcha.engine.CaptchaEngine;
//import net.sf.ehcache.CacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.servlet.Filter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
public class SecurityConfig extends AbstractSecurityConfig {

    @Bean
    public ClientUserRealm userRealm() {
        ClientUserRealm userRealm = new ClientUserRealm();
        userRealm.setCredentialsMatcher(credentialsMatcher());
        userRealm.setAuthenticationCachingEnabled(authenticationCachingEnabled);
        userRealm.setAuthorizationCachingEnabled(authorizationCachingEnabled);
        return userRealm;
    }

    @Bean
    public ClientUserFilter clientUserFilter() {
        ClientUserFilter clientUserFilter = new ClientUserFilter();
        return clientUserFilter;
    }

    @Bean
    public EmailOrSmsCaptchaValidateFilter emailOrSmsCapchaValidateFilter() {
        return new EmailOrSmsCaptchaValidateFilter();
    }

    @Bean
    public RegisterEmailOrMobileValidateFilter registerEmailOrMobileValidateFilter() {
        return new RegisterEmailOrMobileValidateFilter();
    }

    @Bean
    public UserTypeConsistencyValidateFilter userTypeConsistencyValidateFilter() {
        return new UserTypeConsistencyValidateFilter();
    }

  /*  @Bean
    public  MyFormAuthenticationFilter myFormAuthenticationFilter(){
        return  new MyFormAuthenticationFilter();
    }*/

    @Override
    void initRealms(Collection<Realm> realms) {
        realms.add(userRealm());
    }

    @Override
    void initShiroFilterFactoryBean(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        shiroFilterFactoryBean.setLoginUrl("/login");
        Map<String, Filter> mp = new HashMap<>();
        mp.put("sysUser", clientUserFilter());
        mp.put("jCaptchaValidate", jCaptchaValidateFilter());
        mp.put("emailOrSmsValidate", emailOrSmsCapchaValidateFilter());
        mp.put("registerEmailOrMobileValidate", registerEmailOrMobileValidateFilter());
        mp.put("userTypeConsistencyValidateFilter", userTypeConsistencyValidateFilter());
    //    mp.put("authc11", myFormAuthenticationFilter());
        shiroFilterFactoryBean.setFilters(mp);
        Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
        chains.put("/register", "anon");
        chains.put("/resetPassword", "jCaptchaValidate,registerEmailOrMobileValidate,emailOrSmsValidate,anon");
        chains.put("/jcaptcha*", "anon");
        chains.put("/rest/VerificationCode/*", "anon");
        chains.put("/**/favicon.ico*", "anon");
        chains.put("/**/*.css", "anon");
        chains.put("/**/*.js", "anon");
        chains.put("/static/**/*", "anon");
        chains.put("/login", "jCaptchaValidate,userTypeConsistencyValidateFilter,anon");
        chains.put("/logout", "logout");
        //    chains.put("/authenticated", "authc");
        chains.put("/**", "authc,sysUser");
    }

    @Autowired
    private DictionaryService dictionaryService;


    @Override
    public CaptchaEngine getCaptchaEngine() throws Exception {
        Map<String, String> map = dictionaryService.getDictionaryMapByType("FONT_PATH");
        String sunFontPath = ConsoleUtils.isWindows() ?
                map.get("sun_font_path_windows")
                :
                map.get("sun_font_path_others");
        File simSunFontFile = new File(sunFontPath);

        Font font = Font.createFont(Font.TRUETYPE_FONT, simSunFontFile);
        MixedCaptchaEngine.setFont(font);
        return new MixedCaptchaEngine();
    }

    @Bean("MixedCaptchaService")
    @Override
    public ImageCaptchaService captchaService(SpringCacheManagerWrapper cacheManager) throws Exception {
        return super.captchaService(cacheManager);
    }
}