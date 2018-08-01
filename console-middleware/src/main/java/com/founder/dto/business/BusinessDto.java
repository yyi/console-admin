package com.founder.dto.business;

import com.founder.dto.sysadmin.UserDto;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.List;

@Data
public class BusinessDto{
    private Long id;

    private String name;

    private Date createDate;

    private String status;

    private String serviceType;

    private String investorType;

    private String organizationName;

    private String feedback;

    private Long totalScope;

    private String riskLevel;

}
