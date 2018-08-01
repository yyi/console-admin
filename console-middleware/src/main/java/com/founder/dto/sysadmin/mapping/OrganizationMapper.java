package com.founder.dto.sysadmin.mapping;

import com.founder.domain.sysadmin.Organization;
import com.founder.dto.sysadmin.OrganizationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {


    @Mappings(
            @Mapping(source = "parent.id",target = "parentId")
    )
    OrganizationDto OrganizationToOrganizationDto(Organization organization);

    Set<OrganizationDto> OrganizationsToOrganizationDtos(Set<Organization> organizations);

    Organization OrganizationDtoToOrganization(OrganizationDto organizationDto);

}
