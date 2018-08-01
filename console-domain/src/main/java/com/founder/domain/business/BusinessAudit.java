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
@Table(name = "T_BUSINESS_AUDIT")
@Data
@EqualsAndHashCode(exclude = {"bussiness"})
@ToString(exclude = {"bussiness"})
public class BusinessAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String content;

    private Date operationTime;

    private String operationStaff;

    private String filePath;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "BUSINESS_ID")
    private Business bussiness;


    public enum Status {
        UNAUDITED,//未审核
        INAUDITED,//审核中
        AUDITED,//已审核
    }


}
