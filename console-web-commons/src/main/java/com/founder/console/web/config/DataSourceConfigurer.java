package com.founder.console.web.config;


import com.founder.config.annotation.DataSource;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @creaor:yyi
 * @createDate:2019/5/17
 * @Describle
 */
@Component
public class DataSourceConfigurer extends AbstractBeanFactoryAwareAdvisingPostProcessor implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        Pointcut pointcut = new AnnotationMatchingPointcut(null, DataSource.class);
        this.advisor = new DefaultPointcutAdvisor(pointcut, new DynamicDataSourceInterceptor());

    }
}
