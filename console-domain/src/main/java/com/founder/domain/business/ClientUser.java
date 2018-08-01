package com.founder.domain.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.founder.domain.Status;
import com.founder.domain.sysadmin.CredentialedUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "CLIENT_USER")
@Data
@EqualsAndHashCode(exclude = {"businesss"})
@ToString(exclude = {"businesss"})
public class ClientUser implements CredentialedUser {
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

    private Boolean initialized;

    private Boolean personal;

    public String getCredentialsSalt() {
        return loginName + salt;
    }

    public boolean isInitialized() {
        return Objects.isNull(initialized) ? false : initialized;
    }

    public boolean isPersonal() {
        return Objects.isNull(personal) ? false : personal;
    }

    public boolean isPersonalConsistency(Boolean vailPersonal){
        return Objects.equals(personal,vailPersonal );
    }

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "clientUser", fetch = FetchType.LAZY)
    @JsonIgnore
    @OrderBy("createDate desc")
    public List<Business> businesss = Collections.emptyList();

}
