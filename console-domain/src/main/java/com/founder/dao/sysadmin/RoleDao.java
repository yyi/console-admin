package com.founder.dao.sysadmin;

import com.founder.domain.Status;
import com.founder.domain.sysadmin.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDao extends JpaRepository<Role, Long> {
    List<Role> findByStatus(Status status);

    List<Role> findByName(String name);
}
