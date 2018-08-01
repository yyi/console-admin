package com.founder.domain.business;

import com.founder.domain.BusinessType;
import com.founder.domain.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "T_QUESTIONS_VERSION")
@Data
@EqualsAndHashCode(exclude = {"questionss"})
@ToString(exclude = {"questionss"})
public class QuestionsVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String name;

    private Date operationTime;

    private String operationStaff;

    @OneToMany(cascade= {CascadeType.MERGE},fetch=FetchType.LAZY ,mappedBy = "questionsVersion")
    @OrderBy("orders asc")
    private List<Questions> questionss =  Collections.emptyList();


}
