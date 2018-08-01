package com.founder.dao.sysadmin;


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
public interface UserDao extends JpaRepository<User, Long> , QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {

    default void customize(QuerydslBindings bindings, QUser qUser) {

    }

    User findByLoginNameAndStatus(String name, User.Status status);

    Page<User> findByStatus(User.Status status, Pageable pageable);
}
