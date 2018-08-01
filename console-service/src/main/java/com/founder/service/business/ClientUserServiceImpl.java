package com.founder.service.business;

import com.founder.Exception.OperationException;
import com.founder.contract.business.ClientUserService;
import com.founder.dao.business.ClientUserDao;
import com.founder.domain.Status;
import com.founder.domain.business.ClientUser;
import com.founder.exception.sysadmin.SysadminError;
import com.founder.service.sysadmin.PasswordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientUserServiceImpl implements ClientUserService {

    private final ClientUserDao clientUserDao;

    private final PasswordHelper passwordHelper;

    @Override
    public Page<ClientUser> findAll(String loginName, String userName, Pageable pageRequest) {
        Integer clientUserCount = 0;
        //查询所有记录
        List<ClientUser> clientUsersAll = clientUserDao.findAllByLoginNameLikeAndNameLike(loginName, userName);

        if (clientUsersAll != null && !clientUsersAll.isEmpty()) {
            clientUserCount = clientUsersAll.size();
        }
        //分页查询
        List<ClientUser> ClientUsers = clientUserDao.findAllByLoginNameLikeAndNameLike(loginName, userName, pageRequest);

        return new PageImpl<>(ClientUsers, pageRequest, clientUserCount);

    }

    @Override
    public ClientUser createClientUser(ClientUser clientUser) {
        verifyLogName(clientUser);
        clientUser.setCreateTime(new Date());
        clientUser.setStatus(Status.AVAILIABLE);
        clientUser.setInitialized(Boolean.TRUE);
        assignPassword(clientUser);
        persistClientUser(clientUser);
        return clientUser;
    }

    @Override
    public ClientUser updateClientUser(ClientUser clientUser) {
        ClientUser currentClientUser = getUserById(clientUser.getId());
        currentClientUser.setName(clientUser.getName());
        currentClientUser.setPersonal(clientUser.getPersonal());
        currentClientUser.setInitialized(clientUser.getInitialized());
        currentClientUser.setMobileNo(clientUser.getMobileNo());
        currentClientUser.setEmailAddr(clientUser.getEmailAddr());
        return currentClientUser;
    }

    @Override
    public void deleteClientUser(Long id) {
        ClientUser currentClientUser = getUserById(id);
        currentClientUser.setStatus(Status.DISABLE);
    }

    private void verifyLogName(ClientUser clientUser) {
        if (getByLoginNameAndStatus(clientUser.getLoginName(),Status.AVAILIABLE) != null){
            throw new OperationException(SysadminError.LoginNameExists);
        }
    }

    @Override
    public ClientUser findClientUser(String name) {
        return getByLoginName(name);
    }

    @Override
    public ClientUser findClientUserWithNoException(String name) {
        return getByLoginNameAndStatus(name, Status.AVAILIABLE);
    }

    private ClientUser getByLoginName(String name) {
        ClientUser clientUser = getByLoginNameAndStatus(name, Status.AVAILIABLE);
        checkClientUserExists(clientUser);
        return clientUser;
    }

    private ClientUser getByLoginNameAndStatus(String name, Status status) {
        return clientUserDao.findByLoginNameAndStatus(name, status);
    }

    private void checkClientUserExists(ClientUser clientUser) {
        if (Objects.isNull(clientUser)) throw new OperationException(SysadminError.UserNotExists);
    }

    @Override
    public ClientUser createUserByDefaultProfile(String loginName, String passwd, Boolean personal) {
        ClientUser clientUser = createClientUser(loginName, passwd, personal, false);
        persistClientUser(clientUser);
        return clientUser;
    }

    @Override
    public ClientUser changePasswordById(Long id, String password) {
        ClientUser clientUser = getUserById(id);
        changePassword(password, clientUser);
        return clientUser;
    }

    private void changePassword(String password, ClientUser clientUser) {
        clientUser.setPasswd(password);
        assignPassword(clientUser);
    }

    @Override
    public ClientUser changeInitializedPassword(Long id, String passwd) {
        ClientUser clientUser = changePasswordById(id, passwd);
        clientUser.setInitialized(true);
        return clientUser;
    }

    @Override
    public ClientUser changePasswordByLoginName(String loginName, String password) {
        ClientUser clientUser = getByLoginName(loginName);
        changePassword(password, clientUser);
        return clientUser;
    }

    @Override
    public boolean verifyMobileNo(String loginName, String mobileNo) {
        ClientUser clientUser = getByLoginNameAndStatus(loginName, Status.AVAILIABLE);
        return Objects.isNull(clientUser) ? false : StringUtils.equals(mobileNo, clientUser.getMobileNo());
    }

    @Override
    public boolean verifyEmail(String loginName, String email) {
        ClientUser clientUser = getByLoginNameAndStatus(loginName, Status.AVAILIABLE);
        return Objects.isNull(clientUser) ? false : StringUtils.equals(email, clientUser.getEmailAddr());
    }

    private ClientUser getUserById(Long id) {
        ClientUser clientUser = clientUserDao.findById(id).orElse(null);
        checkClientUserExists(clientUser);
        return clientUser;
    }

    private void assignPassword(ClientUser clientUser) {
        passwordHelper.encryptPassword(clientUser);
    }

    private ClientUser createClientUser(String loginName, String passwd, Boolean personal, boolean initialized) {
        ClientUser clientUser = new ClientUser();
        clientUser.setLoginName(loginName);
        clientUser.setPasswd(passwd);
        clientUser.setInitialized(initialized);
        clientUser.setPersonal(personal);
        clientUser.setCreateTime(new Date());
        clientUser.setStatus(Status.AVAILIABLE);
        assignPassword(clientUser);
        return clientUser;
    }

    private void persistClientUser(ClientUser clientUser) {
        clientUserDao.save(clientUser);
    }

}
