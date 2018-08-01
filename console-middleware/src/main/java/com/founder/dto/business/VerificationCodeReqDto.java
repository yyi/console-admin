package com.founder.dto.business;

import lombok.Data;

import java.util.Date;

@Data
public class VerificationCodeReqDto {

    private String loginName;

    private String type;

    private String operationType;

    private String token;

}
