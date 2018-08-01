package com.founder.dao.business;

import com.founder.domain.Status;
import com.founder.domain.business.ClientUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientUserDao extends JpaRepository<ClientUser, Long> {

    ClientUser   findByLoginNameAndStatus(String name,  Status status);

//    @Query("select c from ClientUser c where ( c.loginName like '%'||?1||'%' or null = ?1 ) " +
//            "and ( c.name like '%'||?2||'%' or null = ?2 ) order by c.createTime desc,c.id desc ")
@Query("select c from ClientUser c where ( c.loginName like '%'||?1||'%' or null = ?1 ) " +
        "and ( c.name like '%'||?2||'%' or null = ?2 )  ")
    List<ClientUser> findAllByLoginNameLikeAndNameLike(String loginName,String name);

    @Query("select c from ClientUser c where ( c.loginName like '%'||?1||'%' or null = ?1 ) " +
            "and ( c.name like '%'||?2||'%' or null = ?2 ) ")
    List<ClientUser> findAllByLoginNameLikeAndNameLike(String loginName,String name, Pageable pageRequest);

}
