package com.founder.console.web.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @creaor:yyi
 * @createDate:2019/5/17
 * @Describle
 */
public class MultiDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDB();
    }
}
