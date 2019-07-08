package com.founder.dao.sysadmin;

import com.founder.config.annotation.DataBase;
import com.founder.config.annotation.DataSource;
import com.founder.domain.sysadmin.WikiLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @creaor:yyi
 * @createDate:2019/5/17
 * @Describle
 */
@Repository
public interface WikiLogsDao  extends JpaRepository<WikiLogs, String>  {
    @DataSource(DataBase.presto)
    List<WikiLogs> findWikiLogsByCurrentTitle(String currentTitle);
}
