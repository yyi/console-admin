package com.founder.domain.business;

import com.founder.domain.BusinessType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "T_QUESTIONS")
@Data
@EqualsAndHashCode(exclude = {"answers","questionsVersion"})
@ToString(exclude = {"answers","questionsVersion"})
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Enumerated(EnumType.STRING)
    private QuestionsType questionsType;

    @OneToMany(cascade= {CascadeType.MERGE},fetch=FetchType.LAZY ,mappedBy = "questions")
    @OrderBy("orders asc")
    private Set<Answer> answers =  Collections.emptySet();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "QUESTIONS_VERSION_ID")
    private QuestionsVersion questionsVersion;


    private Long orders;

    public enum Type {

        QUESTIONS_SHARES_EVERYMAN_FINANCIAL,// 股票自然人问卷财务状况
        QUESTIONS_SHARES_EVERYMAN_EXPERIENCE,// 股票自然人问卷投资知识经验
        QUESTIONS_SHARES_EVERYMAN_TARGET,// 股票自然人问卷投资目标
        QUESTIONS_SHARES_EVERYMAN_RISK,// 股票自然人问卷风险偏好
        QUESTIONS_SHARES_ORGANIZATION,// 股票机构问卷
        QUESTIONS_DEPT_ORGANIZATION,// 债务机构问卷

    }
    public enum QuestionsType {
        RADIO,// 单选
        CHECKBOX,// 多选

    }
}
