package com.founder.dto.sysadmin.mapping;

import com.founder.domain.business.Business;
import com.founder.domain.business.BusinessKey;
import com.founder.dto.business.BusinessDto;
import com.founder.dto.business.ValueDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring" ,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BusinessKeyMapper {

    ValueDto businessKeyToValueDto(BusinessKey businessKey);

    List<ValueDto> businessKeysToValueDtos(List<BusinessKey> BusinessKeys);

    BusinessKey valueDtoToBusinessKey(ValueDto valueDto);

}
