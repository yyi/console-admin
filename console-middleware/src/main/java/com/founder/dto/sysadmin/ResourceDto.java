package com.founder.dto.sysadmin;

import lombok.Data;

import java.util.List;

@Data
public class ResourceDto {

    private Long id;

    private String name;

    private String resourceType;

    private String url;

    private String permission;

    private String status;

    private List<ResourceDto> subResources;

    private Long parentId;
}
