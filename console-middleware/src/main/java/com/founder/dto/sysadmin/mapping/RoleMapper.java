package com.founder.dto.sysadmin.mapping;

import com.founder.domain.sysadmin.Role;
import com.founder.dto.sysadmin.RoleDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto roleToRoleDto(Role role);

    Set<RoleDto> rolesToRoleDtos(Set<Role> roles);

    Role RoleDtoToRole(RoleDto roleDto);

    List<RoleDto> rolesToRoleDtos(List<Role> roles);


}
