package com.founder.dao.business;


import com.founder.domain.BusinessType;
import com.founder.domain.business.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessValDao extends JpaRepository<BusinessVal, Long> , QuerydslPredicateExecutor<BusinessVal>, QuerydslBinderCustomizer<QBusinessVal> {

    default void customize(QuerydslBindings bindings, QBusinessVal qBusinessVal) {

    }

    void deleteAllByBusiness(Business business);

    List<BusinessVal> findAllByBusiness(Business business);

    BusinessVal findByBusinessAndAndBusinessKey(Business business,BusinessKey businessKey);


}
