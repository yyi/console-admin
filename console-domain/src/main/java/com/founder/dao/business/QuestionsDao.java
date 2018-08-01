package com.founder.dao.business;


import com.founder.domain.BusinessType;
import com.founder.domain.business.Business;
import com.founder.domain.business.QBusiness;
import com.founder.domain.business.QQuestions;
import com.founder.domain.business.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionsDao extends JpaRepository<Questions, Long> , QuerydslPredicateExecutor<Questions>, QuerydslBinderCustomizer<QQuestions> {

    default void customize(QuerydslBindings bindings, QQuestions qQuestions) {

    }

    List<Questions> findAllByBusinessTypeOrderByOrdersAsc(BusinessType businessType);

}
