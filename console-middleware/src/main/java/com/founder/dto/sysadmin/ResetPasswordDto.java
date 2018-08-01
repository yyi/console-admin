package com.founder.dto.sysadmin;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String userName;
    private String mobileNoOrEmail;
    private String verifyCode;
    private String password;
    private String confirmedPassword;

}
