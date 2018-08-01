package com.founder.domain.business;

import com.founder.domain.BusinessType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "T_BUSINESS_VAL")
@Data
@EqualsAndHashCode(exclude = {"business", "businessKey"})
@ToString(exclude = {"business", "businessKey"})
public class BusinessVal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String val;
    @ManyToOne
    @JoinColumn(name = "BUSINESS_ID")
    private Business business;

    @ManyToOne
    @JoinColumn(name = "BUSINESS_KEY_ID")
    private BusinessKey businessKey;


}
