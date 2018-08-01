package com.founder.dto.sysadmin;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class UserDto {
    private Long id;

    private String loginName;

    private String name;

    private String passwd;

    private Date createTime;

    private Set<RoleDto> roles;

    private Set<OrganizationDto> organizations;

    private List<Long> roleIds;

    private List<Long> organizationIds;

    private List<String> organizationNoList;

    private List<Long> organizationIdList;

}
