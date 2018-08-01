package com.founder.dto.business;

import com.founder.dto.sysadmin.OrganizationDto;
import com.founder.dto.sysadmin.RoleDto;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class ClientUserDto {
    private Long id;

    private String loginName;

    private String name;

    private String passwd;

    private String salt;

    private Date createTime;

    private String status;

    private String mobileNo;

    private String emailAddr;

    private Boolean initialized;

    private Boolean personal;

}
