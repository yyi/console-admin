package com.founder.dto.sysadmin.mapping;

import com.founder.domain.sysadmin.User;
import com.founder.dto.sysadmin.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring" ,unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {RoleMapper.class,OrganizationMapper.class})
public interface UserMapper {

    UserDto userToUserDto(User user);

    List<UserDto> usersToUserDtos(List<User> user);

    User UserDtoToUser(UserDto userDto);

}
