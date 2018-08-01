package com.founder.dao.business;


import com.founder.domain.BusinessType;
import com.founder.domain.business.Business;
import com.founder.domain.business.BusinessKey;
import com.founder.domain.business.QBusiness;
import com.founder.domain.business.QBusinessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessKeyDao extends JpaRepository<BusinessKey, Long> , QuerydslPredicateExecutor<BusinessKey>, QuerydslBinderCustomizer<QBusinessKey> {

    default void customize(QuerydslBindings bindings, QBusinessKey qBusinessKey) {

    }


    List<BusinessKey> findAllByBusinessTypeOrderByOrdersAsc(BusinessType businessType);


}
