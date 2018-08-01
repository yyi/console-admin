package com.founder.domain.business;

import com.founder.domain.BusinessType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "T_BUSINESS_OPERATION")
@Data
@EqualsAndHashCode(exclude = {"business"})
@ToString(exclude = {"business"})
public class BusinessOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    private Long orders;

    @ManyToOne
    @JoinColumn(name = "BUSINESS_ID")
    private Business business;


}
