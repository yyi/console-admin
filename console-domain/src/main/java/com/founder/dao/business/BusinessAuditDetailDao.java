package com.founder.dao.business;


import com.founder.domain.business.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessAuditDetailDao extends JpaRepository<BusinessAuditDetail, Long> , QuerydslPredicateExecutor<BusinessAuditDetail>, QuerydslBinderCustomizer<QBusinessAuditDetail> {

    default void customize(QuerydslBindings bindings, QBusinessAuditDetail qBusinessAuditDetail) {

    }

    BusinessAuditDetail findByBusinessAuditAndKey(BusinessAudit businessAudit,String key);





}
