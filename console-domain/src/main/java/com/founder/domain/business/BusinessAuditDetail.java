package com.founder.domain.business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_BUSINESS_AUDIT_DETAIL")
@Data
@EqualsAndHashCode(exclude = {"businessAudit"})
@ToString(exclude = {"businessAudit"})
public class BusinessAuditDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String key;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "BUSINESS_AUDIT_ID")
    private BusinessAudit businessAudit;


    public enum Status {
        AGREED,    //通过
        REJECTIVE,  //不通过
        NOT_APPLICABLE //此项不适用
    }


}
