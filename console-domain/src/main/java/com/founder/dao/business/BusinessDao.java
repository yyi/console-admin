package com.founder.dao.business;


import com.founder.domain.business.Business;
import com.founder.domain.business.ClientUser;
import com.founder.domain.business.QBusiness;
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
public interface BusinessDao extends JpaRepository<Business, Long> , QuerydslPredicateExecutor<Business>, QuerydslBinderCustomizer<QBusiness> {

    default void customize(QuerydslBindings bindings, QBusiness qBusiness) {

    }

    List<Business> findAllByClientUserAndStatusInOrderByCreateDateDesc(ClientUser clientUser,List<Business.Status> statuss);

}
