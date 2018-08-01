package com.founder.shiro.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

@Data
@AllArgsConstructor
public class OaUserToken extends UsernamePasswordToken {

    private String oaUserName;

    private String timeStamp;

}
