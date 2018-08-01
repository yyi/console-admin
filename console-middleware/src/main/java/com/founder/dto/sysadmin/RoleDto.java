package com.founder.dto.sysadmin;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class RoleDto {
    private Long id;

    private String name;

    private String description;

    private String status;

    private List<Long> resourceIds = Collections.emptyList();
}
