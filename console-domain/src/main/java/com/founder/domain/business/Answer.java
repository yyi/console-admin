package com.founder.domain.business;

import com.founder.domain.BusinessType;
import com.founder.domain.sysadmin.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "T_ANSWER")
@Data
@EqualsAndHashCode(exclude = {"businesss"})
@ToString(exclude = {"businesss"})
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long score;

    private Long orders;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "QUESTIONS_ID")
    private Questions questions;

    @ManyToMany(mappedBy = "answers", fetch = FetchType.EAGER)
    public Set<Business> businesss = Collections.emptySet();

}
