package com.founder.dto.business;

import com.founder.dto.sysadmin.UserDto;
import lombok.Data;

import java.util.Date;

@Data
public class ValueDto {
    private Long id;

    private String key;

    private String displayName;

    private String val ="";

    private String auditResult;
}
