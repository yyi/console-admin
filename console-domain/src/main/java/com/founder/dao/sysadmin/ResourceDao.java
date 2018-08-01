package com.founder.dao.sysadmin;

import com.founder.domain.sysadmin.Resource;
import com.founder.domain.sysadmin.ResourceValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceDao extends JpaRepository<Resource, Long> {

    @Query(nativeQuery = true)
    List<ResourceValue> findRoleResource( List<Long>roleIds);
    @Query(nativeQuery = true)
    List<String> findRolePermission(List<Long> roleIds);

}
