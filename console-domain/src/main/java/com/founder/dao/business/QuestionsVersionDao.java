package com.founder.dao.business;


import com.founder.domain.BusinessType;
import com.founder.domain.Status;
import com.founder.domain.business.QQuestions;
import com.founder.domain.business.QQuestionsVersion;
import com.founder.domain.business.Questions;
import com.founder.domain.business.QuestionsVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionsVersionDao extends JpaRepository<QuestionsVersion, Long> , QuerydslPredicateExecutor<QuestionsVersion>, QuerydslBinderCustomizer<QQuestionsVersion> {

    default void customize(QuerydslBindings bindings, QQuestionsVersion qQuestionsVersion) {

    }

    QuestionsVersion findFirstByBusinessTypeAndStatusOrderByIdDesc(BusinessType businessType, Status status);



}
