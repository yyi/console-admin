package com.founder.dto.business;

import com.founder.domain.business.BusinessAudit;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Data
public class BusinessAuditDto {

    private Long id;

    private String content;

    private Date operationTime;

    private String operationStaff;

    private String filePath;

}
