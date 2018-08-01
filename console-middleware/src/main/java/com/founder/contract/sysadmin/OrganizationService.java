package com.founder.contract.sysadmin;

import com.founder.domain.sysadmin.Organization;
import com.founder.domain.sysadmin.OrganizationValue;
import com.founder.dto.sysadmin.OrganizationDto;
import com.founder.dto.sysadmin.OrganizationReqDto;

import java.util.List;
import java.util.Set;

public interface OrganizationService {

    List<Organization> findOrganizationsByOrganizationNoIn(List<String> organizations);

    Set<Organization> findOrganizationsByIds(Iterable<Long> ids);

    List<OrganizationValue> findAllOrganizations();

    List<Organization> findAllOrganizationList();

    Organization create(Organization organization, Long parentId, String userName);

    void update(OrganizationDto organizationDto);

    void updateParent(OrganizationReqDto organizationReqDto);

    void delete(Long id);

    Organization findRootOragination();

    OrganizationDto findRootOrganizationDto();

    Organization findOrganizationByNo(String organizationNo);

    OrganizationDto configOrganizationDto(OrganizationDto organizationDto, Long id);

}
