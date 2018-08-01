package com.founder.contract.business;

import com.founder.domain.business.ClientUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientUserService {

    public ClientUser findClientUser(String name);


    ClientUser findClientUserWithNoException(String name);

    ClientUser createUserByDefaultProfile(String loginName, String passwd, Boolean personal);

    ClientUser changePasswordById(Long id, String passwd);

    ClientUser changeInitializedPassword(Long id, String passwd);

    Page<ClientUser> findAll(String loginName, String userName, Pageable pageRequest);

    ClientUser changePasswordByLoginName(String loginName, String password);

    boolean verifyMobileNo(String loginName, String mobileNo);

    boolean verifyEmail(String loginName, String email);

    /**
     * 新增客户信息
     * @param clientUser
     * @return
     */
    ClientUser createClientUser(ClientUser clientUser);

    /**
     * 修改客户信息
     * @param clientUser
     * @return
     */
    ClientUser updateClientUser(ClientUser clientUser);

    /**
     * 删除客户信息，只改状态
     * @param id
     */
    void deleteClientUser(Long id);

}
