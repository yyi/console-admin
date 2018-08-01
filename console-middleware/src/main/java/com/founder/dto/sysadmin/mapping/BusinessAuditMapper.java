package com.founder.dto.sysadmin.mapping;

import com.founder.domain.business.Business;
import com.founder.domain.business.BusinessAudit;
import com.founder.dto.business.BusinessAuditDto;
import com.founder.dto.business.BusinessDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring" ,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BusinessAuditMapper {

    BusinessAuditDto businessAuditToBusinessAuditDto(BusinessAudit businessAudit);

    BusinessAudit BusinessAuditDtoToBusinessAudit(BusinessAuditDto businessAuditDto);

}
