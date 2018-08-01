package com.founder.dao.business;


import com.founder.domain.business.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerDao extends JpaRepository<Answer, Long> , QuerydslPredicateExecutor<Answer>, QuerydslBinderCustomizer<QAnswer> {

    default void customize(QuerydslBindings bindings, QAnswer qAnswer) {

    }



}
