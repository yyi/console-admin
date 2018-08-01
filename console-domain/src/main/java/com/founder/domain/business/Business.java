package com.founder.domain.business;

import com.founder.domain.BusinessType;
import com.founder.domain.sysadmin.Organization;
import com.founder.domain.sysadmin.Resource;
import com.founder.domain.sysadmin.Role;
import com.founder.domain.sysadmin.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "T_BUSINESS")
@Data
@EqualsAndHashCode(exclude = {"clientUser","answers"})
@ToString(exclude = {"clientUser","answers"})
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Date createDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private InvestorType investorType;



    private String organizationName;

    private String feedback;

    private Long totalScope;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;


    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name="USER_ID")
    private ClientUser clientUser;


    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JoinTable(name = "T_BUSINESS_ANSWER",
            joinColumns = {@JoinColumn(name = "BUSINESS_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ANSWER_ID")})
    private Set<Answer> answers = Collections.emptySet();

    public enum Status {
        AUDIT_YES,//审核通过
        AUDIT_NO,//审核不通过
        COMPLETE,//待审核
        UNFINISHED,//未完成;
        DISABLE,//禁用
    }

    public enum ServiceType {
        SHARES,//股票
        DEPT,//债务
        ;
    }

    public enum InvestorType {
        DEPT_SPECIALTY, //债务专业
        DEPT_ORDINARY,//债务普通
        SHARES_A_SPECIALTY,//股票A类专业投资者
        SHARES_B_SPECIALTY,//股票B类专业投资者
        SHARES_ORDINARY,//股票普通 ;
        SHARES_SPECIALTY//股票专业 ;
    }

    public enum RiskLevel {
      C1, //
      C2,//
      C3,//
      C4,//
      C5,// ;
      C6// ;
    }

}
