package com.founder.console.web.config;

import com.founder.config.annotation.DataBase;

/**
 * @creaor:yyi
 * @createDate:2019/5/17
 * @Describle
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<DataBase> contextHolder = new ThreadLocal<>();


    // 设置数据源名
    public static void setDB(DataBase dbType) {
        contextHolder.set(dbType);
    }

    // 获取数据源名
    public static DataBase getDB() {
        return (contextHolder.get());
    }

    // 清除数据源名
    public static void clearDB() {
        contextHolder.remove();
    }


}
