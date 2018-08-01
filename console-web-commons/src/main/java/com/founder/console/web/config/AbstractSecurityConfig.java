package com.founder.console.web.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.founder.console.web.captcha.*;
import com.founder.shiro.autenticator.FirstExceptionStrategy;
import com.founder.shiro.credentials.RetryLimitHashedCredentialsMatcher;
import com.founder.shiro.spring.SpringCacheManagerWrapper;
import com.octo.captcha.engine.CaptchaEngine;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import java.time.Duration;
import java.util.*;

public abstract class AbstractSecurityConfig {

    @Value("${console.security.algorithmName:md5}")
    protected String algorithmName;
    @Value("${console.security.hashIterations:2}")
    protected int hashIterations;
    @Value("${console.security.authenticationCachingEnabled:false}")
    protected boolean authenticationCachingEnabled;
    @Value("${console.security.authorizationCachingEnabled:false}")
    protected boolean authorizationCachingEnabled;
    @Value("${server.session.timeout:150}")
    protected long sessionTimeout;


    @Autowired
    private RedisConnectionFactory connectionFactory = null;


    @Bean
    public RedisCacheManager initRedisCacheManager() {
        // Redis加锁的写入器
        RedisCacheWriter writer= RedisCacheWriter.lockingRedisCacheWriter(connectionFactory);
        // 启动Redis缓存的默认设置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 设置JDK序列化器
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer()));
        // 禁用前缀
        config = config.disableKeyPrefix();
        //设置30分钟超时
        config = config.entryTtl(Duration.ofMinutes(30));
        // 创建缓Redis存管理器
        RedisCacheManager redisCacheManager = new RedisCacheManager(writer, config);
        return redisCacheManager;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache/ehcache.xml"));
        return ehCacheManagerFactoryBean;
    }

//    @Bean
//    public CacheManager ehcacheManager() {
//        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
//        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache/ehcache.xml"));
//        return ehCacheManagerFactoryBean.getObject();
//    }


    @Bean("springcache")
    public EhCacheCacheManager springCacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        ehCacheCacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
        return ehCacheCacheManager;
    }


    @Bean
    public SpringCacheManagerWrapper springCacheManagerWrapper() {
        SpringCacheManagerWrapper springCacheManagerWrapper = new SpringCacheManagerWrapper();
        springCacheManagerWrapper.setCacheManager(initRedisCacheManager());
        return springCacheManagerWrapper;
    }

    @Bean
    public RetryLimitHashedCredentialsMatcher credentialsMatcher() {
        RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher(springCacheManagerWrapper());
        retryLimitHashedCredentialsMatcher.setHashAlgorithmName(algorithmName);
        retryLimitHashedCredentialsMatcher.setHashIterations(hashIterations);
        retryLimitHashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return retryLimitHashedCredentialsMatcher;
    }


    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    @Bean
    public SimpleCookie sessionIdCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("sid");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(-1);
        return simpleCookie;
    }

//    @Bean
//    public SimpleCookie rememberMeCookie() {
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        simpleCookie.setHttpOnly(true);
//        simpleCookie.setMaxAge(2592000);
//        return simpleCookie;
//    }

//    @Bean
//    public CookieRememberMeManager rememberMeManager() {
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        cookieRememberMeManager.setCipherKey(org.apache.shiro.codec.Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
//        cookieRememberMeManager.setCookie(rememberMeCookie());
//        return cookieRememberMeManager;
//    }

    @Bean
    public EnterpriseCacheSessionDAO sessionDAO() {
        EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
        enterpriseCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
        enterpriseCacheSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        return enterpriseCacheSessionDAO;
    }

//    @Bean
//    public DefaultWebSessionManager sessionManager() {
//        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
//        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);
//        defaultWebSessionManager.setGlobalSessionTimeout(sessionTimeout * 1000);
//        defaultWebSessionManager.setDeleteInvalidSessions(true);
//        defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
//        //   defaultWebSessionManager.setSessionValidationScheduler(sessionValidationScheduler());
//        defaultWebSessionManager.setSessionDAO(sessionDAO());
//        defaultWebSessionManager.setSessionIdCookieEnabled(true);
//        defaultWebSessionManager.setSessionIdCookie(sessionIdCookie());
//        return defaultWebSessionManager;
//    }

    @Bean(name = "sessionManager")
    public ServletContainerSessionManager sessionManager() {
        ServletContainerSessionManager sessionManager = new ServletContainerSessionManager();
        return sessionManager;
    }

//    @Bean
//    public QuartzSessionValidationScheduler sessionValidationScheduler() {
//        QuartzSessionValidationScheduler quartzSessionValidationScheduler = new QuartzSessionValidationScheduler();
//        quartzSessionValidationScheduler.setSessionValidationInterval(1800000);
//        quartzSessionValidationScheduler.setSessionManager(sessionManager());
//        return quartzSessionValidationScheduler;
//    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setAuthenticator(authenticator());
        Collection<Realm> realms = new ArrayList<>();
        initRealms(realms);
        defaultWebSecurityManager.setRealms(realms);
        defaultWebSecurityManager.setSessionManager(sessionManager());
        defaultWebSecurityManager.setCacheManager(springCacheManagerWrapper());
        //   defaultWebSecurityManager.setRememberMeManager(rememberMeManager());
        return defaultWebSecurityManager;
    }

    abstract void initRealms(Collection<Realm> realms);

    private Authenticator authenticator() {
        ModularRealmAuthenticator modularRealmAuthenticator = new ModularRealmAuthenticator();
        modularRealmAuthenticator.setAuthenticationStrategy(firstExceptionStrategy());
        return modularRealmAuthenticator;
    }

    private AuthenticationStrategy firstExceptionStrategy() {
        return new FirstExceptionStrategy();
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        methodInvokingFactoryBean.setArguments(new DefaultWebSecurityManager[]{securityManager()});
        return methodInvokingFactoryBean;
    }

//    @Bean
//    public FormAuthenticationFilter formAuthenticationFilter() {
//        LoginFormAuthenticationFilter formAuthenticationFilter = new LoginFormAuthenticationFilter();
//        formAuthenticationFilter.setUsernameParam("username");
//        formAuthenticationFilter.setPasswordParam("password");
//        //formAuthenticationFilter.setRememberMeParam("rememberMe");
//        formAuthenticationFilter.setFailureKeyAttribute("shiroLoginFailure");
//        return formAuthenticationFilter;
//    }


    @Bean
    public CaptchaValidateFilter jCaptchaValidateFilter() {
        CaptchaValidateFilter jCaptchaValidateFilter = new CaptchaValidateFilter();
        jCaptchaValidateFilter.setJcaptchaEbabled(true);
        jCaptchaValidateFilter.setJcaptchaParam("jcaptchaCode");
        jCaptchaValidateFilter.setFailureKeyAttribute("shiroLoginFailure");
        return jCaptchaValidateFilter;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        Map<String, String> chains = new HashMap<>();
        chains.put("/druid/*", "anon");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(chains);
        initShiroFilterFactoryBean(shiroFilterFactoryBean);
        return shiroFilterFactoryBean;
    }

    abstract void initShiroFilterFactoryBean(ShiroFilterFactoryBean shiroFilterFactoryBean);


//    @Bean(name = "lifecycleBeanPostProcessor")
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
//        LifecycleBeanPostProcessor lifecycleBeanPostProcessor = new LifecycleBeanPostProcessor();
//        return lifecycleBeanPostProcessor;
//    }


//    @Bean(name = "defaultAdvisorAutoProxyCreator")
//    @DependsOn("lifecycleBeanPostProcessor")
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
//        proxyCreator.setProxyTargetClass(true);
//        return proxyCreator;
//    }
//

    @Bean
    public FilterRegistrationBean registrationShiroFileter() throws Exception {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new DelegatingFilterProxy("shiroFilter"));
        filterRegistrationBean.setEnabled(true);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        filterRegistrationBean.setUrlPatterns(urlPatterns);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);

        return filterRegistrationBean;
    }


    @Bean
    public FilterRegistrationBean registrationJCaptchaFilter() throws Exception {
        FilterRegistrationBean registration = new FilterRegistrationBean();

        registration.setFilter(new CaptchaFilter());
        registration.addUrlPatterns("/jcaptcha.jpg");
        registration.setName("CaptchaFilter");
        registration.setAsyncSupported(true);

        return registration;
    }



    ImageCaptchaService captchaService(SpringCacheManagerWrapper cacheManager) throws Exception {
        ImageCaptchaService ics = new ImageCaptchaService(new RedisCaptchaStore(cacheManager.getCache("captchaStoreCache")), getCaptchaEngine(), 180, 100000, 75000);
        CaptchaUtile.init(ics);
        return ics;
    }

    abstract  CaptchaEngine getCaptchaEngine() throws Exception;


    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
}
