package com.founder.domain.sysadmin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class ResourceValue implements Serializable {
    private Long id;

    private Long parentId;

    private String name;

    private String type;

    private String url;

    private String permission;

    private String status;

    private Long level;
}
