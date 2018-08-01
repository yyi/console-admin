package com.founder.dao.business;


import com.founder.domain.business.*;
import com.founder.domain.sysadmin.QUser;
import com.founder.domain.sysadmin.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessAuditDao extends JpaRepository<BusinessAudit, Long> , QuerydslPredicateExecutor<BusinessAudit>, QuerydslBinderCustomizer<QBusinessAudit> {

    default void customize(QuerydslBindings bindings, QBusinessAudit qBusinessAudit) {

    }

    BusinessAudit findFirstByBussinessOrderByOperationTimeDesc(Business business);

    BusinessAudit findFirstByBussinessAndStatusOrderByOperationTimeDesc(Business business,BusinessAudit.Status status);





}
