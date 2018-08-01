package com.founder.domain.sysadmin;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "SYS_USER")
@Data
@EqualsAndHashCode(exclude = {"organizations", "roles"})
public class User implements CredentialedUser, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String loginName;

    private String name;

    private String passwd;

    private String salt;

    private Date createTime;


    @Enumerated(EnumType.STRING)
    private Status status;

    private String mobileNo;

    private String emailAddr;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SYS_USER_ORGANIZATION",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ORGANIZATION_ID")})
    private Set<Organization> organizations = Collections.emptySet();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SYS_USER_ROLE",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")})
    private Set<Role> roles = Collections.emptySet();

    public String getCredentialsSalt() {
        return loginName + salt;
    }

    public boolean isLocked() {
        return status == Status.LOCKED;
    }

    public enum Status {
        AVAILIABLE, DISABLE, LOCKED;
    }

    public enum UserType {
        GENERAL_USER, MANAGER_USER,;
    }
}
