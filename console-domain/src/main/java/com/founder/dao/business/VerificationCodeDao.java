package com.founder.dao.business;

import com.founder.domain.business.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VerificationCodeDao extends JpaRepository<VerificationCode, Long>  {

    List<VerificationCode> getByTokenAndTypeAndOperationTypeAndStatusOrderByCreateTimeDesc(String token, VerificationCode.Type type,
                                                                                           VerificationCode.OperationType operationType,
                                                                                           VerificationCode.Status status);
    List<VerificationCode> getByTokenAndStatusOrderByCreateTimeDesc(String token,VerificationCode.Status status);

    List<VerificationCode> getAllByTokenAndCreateTimeAfter(String token,Date createTime);

    List<VerificationCode> getAllByToken(String token);
}
