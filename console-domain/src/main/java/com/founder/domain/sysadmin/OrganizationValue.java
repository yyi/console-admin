package com.founder.domain.sysadmin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class OrganizationValue implements Serializable {

    private Long id;

    private Long parentId;

    private String name;

    private String status;

    private String organizationType;

    private Long organizationOrder;

    private String organizationNo;

    private String address;

    private String zipCode;

    private Long level;

    private String organizationLevel;

}
