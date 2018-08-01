package com.founder.domain.sysadmin;

import java.io.Serializable;

public interface CredentialedUser extends Serializable {
    void  setSalt(String salt);
    String getPasswd();
    String getCredentialsSalt();
    void setPasswd(String passwd);
}
