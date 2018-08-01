package com.founder.domain.sysadmin;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "T_DICTIONARY")
@Data
public class Dictionary implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String dtKey;

    private String dtValue;

    private String type;

    private String remark;

    private Long dtOrder;
}
