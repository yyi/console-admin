package com.founder.contract.sysadmin;

public interface DecryptMessage {

    default String decrypt(String encryptStr) {
        return encryptStr;
    }

}
