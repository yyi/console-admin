package com.founder.console.web.config;



import com.founder.config.annotation.DataBase;
import com.founder.config.annotation.DataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @creaor:yyi
 * @createDate:2019/5/17
 * @Describle
 */
public class DynamicDataSourceInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        DataBase dataBase = DataBase.oracle;
        try {
            if (method.isAnnotationPresent(DataSource.class)) {
                DataSource annotation = method.getAnnotation(DataSource.class);
                // 取出注解中的数据源名
                dataBase = annotation.value();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 切换数据源
        DataSourceContextHolder.setDB(dataBase);

        Object proceed = invocation.proceed();

        DataSourceContextHolder.clearDB();

        return proceed;

    }
}
