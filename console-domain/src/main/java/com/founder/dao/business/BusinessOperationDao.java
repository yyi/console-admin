package com.founder.dao.business;


import com.founder.domain.business.Business;
import com.founder.domain.business.BusinessOperation;
import com.founder.domain.business.QBusiness;
import com.founder.domain.business.QBusinessOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessOperationDao extends JpaRepository<BusinessOperation, Long> , QuerydslPredicateExecutor<BusinessOperation>, QuerydslBinderCustomizer<QBusinessOperation> {

    default void customize(QuerydslBindings bindings, QBusinessOperation qBusinessOperation) {

    }

    List<BusinessOperation> findAllByBusinessOrderByOrdersAsc(Business business);


}
