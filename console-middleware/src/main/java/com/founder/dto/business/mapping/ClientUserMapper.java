package com.founder.dto.business.mapping;

import com.founder.domain.business.ClientUser;
import com.founder.dto.business.ClientUserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring" )
public interface ClientUserMapper {

    ClientUserDto clientUserToClientUserDto(ClientUser clientUser);

    List<ClientUserDto> clientUsersToClientUserDtos(List<ClientUser> clientUsers);

    ClientUser clientUserDtoToClientUser(ClientUserDto clientUserDto);

}
