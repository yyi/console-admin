package com.founder.domain.business;

import com.founder.domain.BusinessType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_BUSINESS_KEY")
@Data
public class BusinessKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String key;


    private String displayName;

    private Long orders;


    @Enumerated(EnumType.STRING)
    private BusinessType businessType;
}
