package com.founder.dto.sysadmin;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class OrganizationDto {
    private Long id;

    private Long parentId;

    private String name;

    private String status;

    private String organizationNo;

    private String organizationType;

    private String organizationLevel;

    private Long organizationOrder;

    private String address;

    private String zipCode;

    private List<OrganizationDto> subOrganization = new ArrayList<>();

    public void appendSubOrgnazation(OrganizationDto organizationDto) {
        subOrganization.add(organizationDto);
    }
}
