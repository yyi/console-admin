package com.founder.dto.sysadmin.mapping;

import com.founder.domain.sysadmin.Resource;
import com.founder.dto.sysadmin.ResourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mappings(
            @Mapping(source = "parent.id",target = "parentId")
    )
    ResourceDto resourceToResourceDto(Resource resource);

    Resource resourceDtoToResource(ResourceDto resourceDto);

    Set<ResourceDto> resourcesToResourceDos(Set<Resource> resourceSet);

}
