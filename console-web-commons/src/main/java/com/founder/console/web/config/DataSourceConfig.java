package com.founder.console.web.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.founder.contract.sysadmin.DecryptMessage;
import com.founder.service.sysadmin.AesDecryptMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;

@Configuration
@Log4j2
public class DataSourceConfig {
    @Value("${console.security.jdbc.encrypt:false}")
    private boolean encrypt;

    private DecryptMessage decryptMessage() {
        return encrypt ? new AesDecryptMessage() : new DecryptMessage() {
        };
    }

    @Bean
    public DataSource druidDataSource(@Value("${spring.datasource.driver-class-name}") String driver,
                                      @Value("${spring.datasource.url}") String url, @Value("${spring.datasource.username}") String username,
                                      @Value("${spring.datasource.password}") String password,
                                      @Value("${spring.datasource.filters}") String filters,
                                      @Value("${spring.datasource.maxActive}") int maxActive,
                                      @Value("${spring.datasource.initialSize}") int initialSize,
                                      @Value("${spring.datasource.maxWait}") long maxWait, @Value("${spring.datasource.minIdle}") int minIdle,
                                      @Value("${spring.datasource.timeBetweenEvictionRunsMillis}") int timeBetweenEvictionRunsMillis,
                                      @Value("${spring.datasource.minEvictableIdleTimeMillis}") long minEvictableIdleTimeMillis,
                                      @Value("${spring.datasource.validationQuery}") String validationQuery,
                                      @Value("${spring.datasource.testWhileIdle}") boolean testWhileIdle,
                                      @Value("${spring.datasource.testOnBorrow}") boolean testOnBorrow,
                                      @Value("${spring.datasource.testOnReturn}") boolean testOnReturn,
                                      @Value("${spring.datasource.poolPreparedStatements}") boolean poolPreparedStatements,
                                      @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}") int maxOpenPreparedStatements,
                                      @Value("${spring.datasource.connectionProperties}") String connectionProperties) {
        DecryptMessage decryptMessage = decryptMessage();
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        String jdbcUrl = decryptMessage.decrypt(url);
        String decryptedUserName = decryptMessage.decrypt(username);
        String decryptedPassword = decryptMessage.decrypt(password);
        log.info("jdbcUrl:{},userName:{}", () -> jdbcUrl, () -> decryptedUserName);
        druidDataSource.setUrl(jdbcUrl);
        druidDataSource.setUsername(decryptedUserName);
        druidDataSource.setPassword(decryptedPassword);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        druidDataSource.setConnectionProperties(connectionProperties);
        try {
            druidDataSource.setFilters(filters);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return druidDataSource;
    }


    @Value("${console.security.druid.allow:0.0.0.0}")
    private String allow;

    @Value("${console.security.druid.loginName:admin}")
    private String loginName;

    @Value("${console.security.druid.password:ain210191addbbcddf22}")
    private String password;

    @Value("${console.security.druid.resetEnable:true}")
    private String resetEnable;

    @Value("${console.security.druid.urlMappings:/druid/*}")
    private String urlMappings;

    @Bean
    @ConditionalOnProperty(prefix = "console.datasource.monitor", name = "enable", havingValue = "true", matchIfMissing = true)
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), urlMappings);
        if (!Objects.equals("0.0.0.0", allow))
            servletRegistrationBean.addInitParameter("allow", allow);
        servletRegistrationBean.addInitParameter("loginUsername", loginName);
        servletRegistrationBean.addInitParameter("loginPassword", password);
        servletRegistrationBean.addInitParameter("resetEnable", resetEnable);
        return servletRegistrationBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "console.datasource.monitor", name = "enable", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean statFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "console.datasource.monitor", name = "enable", havingValue = "true", matchIfMissing = true)
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnProperty(prefix = "console.datasource.monitor", name = "enable", havingValue = "true", matchIfMissing = true)
    public JdkRegexpMethodPointcut jdkRegexpMethodPointcut() {
        JdkRegexpMethodPointcut jdkRegexpMethodPointcut = new JdkRegexpMethodPointcut();
        jdkRegexpMethodPointcut.setPattern("com.founder.*");
        return jdkRegexpMethodPointcut;
    }

    @Bean
    @ConditionalOnProperty(prefix = "console.datasource.monitor", name = "enable", havingValue = "true", matchIfMissing = true)
    public DefaultPointcutAdvisor defaultPointcutAdvisor() {
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setAdvice(druidStatInterceptor());
        defaultPointcutAdvisor.setPointcut(jdkRegexpMethodPointcut());
        return defaultPointcutAdvisor;
    }


}
