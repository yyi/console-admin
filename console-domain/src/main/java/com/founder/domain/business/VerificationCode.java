package com.founder.domain.business;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_VERIFICATION_CODE")
@Data
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    private String code;

    private String loginName;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date createTime;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Date invaiidTime;

    private String content;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    private String ip;

    public enum Status {
        AVAILIABLE, DISABLE;
    }

    public enum Type {
        MAIL, //邮件
        SMS; //短信
    }

    public enum OperationType {
        LOGIN, //登陆
        REGISTER,//注册
        RESET_PASSWORD,; //找回密码
    }

}
