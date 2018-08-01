package com.founder.contract.business;

import com.founder.domain.business.VerificationCode;

public interface VerificationCodeService {

    void create(String loginName,VerificationCode.Type type, VerificationCode.OperationType operationType, String token, String ip);

    boolean verify(String token,String code);

}
