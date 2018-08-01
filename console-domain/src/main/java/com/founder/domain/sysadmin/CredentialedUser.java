package com.founder.domain.sysadmin;

public interface CredentialedUser {
    void  setSalt(String salt);
    String getPasswd();
    String getCredentialsSalt();
    void setPasswd(String passwd);
}
