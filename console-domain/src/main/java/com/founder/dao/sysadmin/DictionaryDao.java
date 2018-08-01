package com.founder.dao.sysadmin;
import com.founder.domain.sysadmin.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryDao extends JpaRepository<Dictionary, Long> {
    List<Dictionary> getByTypeOrderByDtOrder(String type);
}
