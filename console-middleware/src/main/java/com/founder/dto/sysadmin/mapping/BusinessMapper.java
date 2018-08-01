package com.founder.dto.sysadmin.mapping;

import com.founder.domain.business.Business;
import com.founder.domain.sysadmin.User;
import com.founder.dto.business.BusinessDto;
import com.founder.dto.sysadmin.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring" ,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BusinessMapper {

    BusinessDto businessToBusinessDto(Business business);

    Set<BusinessDto> businesssToBusinessDtos(Set<Business> businesss);

    List<BusinessDto> businesssToBusinessDtos(List<Business> businesss);

    Business BusinessDtoToBusiness(BusinessDto businessDto);

}
