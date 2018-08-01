package com.founder.dao.sysadmin;

import com.founder.domain.sysadmin.Organization;
import com.founder.domain.sysadmin.OrganizationValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationDao extends JpaRepository<Organization,Long> {


    @Query(nativeQuery = true)
    List<OrganizationValue> findAllOrganizations();
    @Query(nativeQuery = true)
    List<OrganizationValue> findByUserId(Long userId);
    List<Organization> findOrganizationsByOrganizationNoIn(List<String> organizations);
    Organization findOrganizationByOrganizationNo(String organizationNo);

}
