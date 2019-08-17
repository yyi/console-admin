package com.founder.service.sysadmin;

import com.founder.Exception.OperationException;
import com.founder.config.WhereClauseBuilder;
import com.founder.contract.sysadmin.OrganizationService;
import com.founder.contract.sysadmin.RoleService;
import com.founder.contract.sysadmin.UserService;
import com.founder.dao.business.TestKt;
import com.founder.dao.sysadmin.ResourceDao;
import com.founder.dao.sysadmin.UserDao;
import com.founder.domain.sysadmin.Organization;
import com.founder.domain.sysadmin.QUser;
import com.founder.domain.sysadmin.Role;
import com.founder.domain.sysadmin.User;
import com.founder.dto.sysadmin.UserDto;
import com.founder.dto.sysadmin.mapping.UserMapper;
import com.founder.exception.sysadmin.SysadminError;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {


    private final UserDao userDao;


    private final ResourceDao resourceDao;


    private final PasswordHelper passwordHelper;


    private final RoleService roleService;


    private final OrganizationService organizationService;


    private final UserMapper userMapper;

    //List<Organization> childOrganization = new ArrayList<>();

    private static ThreadLocal<List<Organization>> childOrganizationThread = new ThreadLocal<List<Organization>>() {
        protected List<Organization> initialValue() {
            return new ArrayList<Organization>();
        }
    };

    private static ThreadLocal<Integer> countThread = new ThreadLocal<Integer>() {
        protected Integer initialValue() {
            return 0;
        }
    };

    //private int recursiveCount = 0;

    @Override
    public User createUser(User user, List<Long> roleIds, List<Long> organizationIds) {
        verifyLogName(user);
        resetUser(user);
        assignPassword(user);
        assignRoles(user, roleIds);
        assignOrganizations(user, organizationIds);
        return persistUser(user);
    }

    private User persistUser(User user) {
        return userDao.save(user);
    }

    private void assignPassword(User user) {
        passwordHelper.encryptPassword(user);
    }

    private void verifyLogName(User user) {
        if (findByUsername(user.getLoginName()) != null)
            throw new OperationException(SysadminError.LoginNameExists);
    }

    private void resetUser(User user) {
        user.setId(null);
        user.setCreateTime(new Date());
        user.setStatus(User.Status.AVAILIABLE);
    }

    private void assignOrganizations(User user, List<Long> organizationIds) {
        user.setOrganizations(organizationService.findOrganizationsByIds(organizationIds));
    }

    private void assignRoles(User user, List<Long> roleids) {
        user.setRoles(roleService.findRolesByIds(roleids));
    }

    @Override
    public User updateUser(User user, List<Long> roleIds, List<Long> organizationIds) {
        User currentUser = getUserById(user.getId());
        assignRoles(user, roleIds);
        assignOrganizations(user, organizationIds);
        currentUser.setName(user.getName());
        currentUser.setRoles(user.getRoles());
        currentUser.setOrganizations(user.getOrganizations());
        return currentUser;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        user.setStatus(User.Status.DISABLE);
    }

    @Override
    public void changePassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPasswd(newPassword);
        assignPassword(user);
    }

    private User getUserById(Long userId) {
        Optional<User> user = userDao.findById(userId);
        if (!user.isPresent()) {
            throw new OperationException(SysadminError.UserNotExists);
        }
        return user.get();
    }

    @Override
    public User findOne(Long userId) {
        return userDao.findById(userId).orElse(null);
    }

    @Override
    public Page<User> findAll(String loginName, String userName, Pageable pageRequest) {
        TestKt.test();
        QUser user = QUser.user;
        User.Status available = User.Status.AVAILIABLE;
        return userDao.findAll(
                new WhereClauseBuilder().optionalAnd(loginName, () -> user.loginName.likeIgnoreCase(Expressions.asString("%").concat(loginName).concat("%")))
                        .optionalAnd(userName, () -> user.name.likeIgnoreCase(Expressions.asString("%").concat(userName).concat("%")))
                        .optionalAnd(available, () -> user.status.eq(available))
                , pageRequest);
    }

    @Override
    public User findByUsername(String username) {
        User user = userDao.findByLoginNameAndStatus(username, User.Status.AVAILIABLE);
        //初始化所有子部门的id和no
        return user;
    }

    @Override
    public UserDto converUserToUserDto(User user) {
        UserDto userDto = null;
        try {
            user = userDao.findById(user.getId()).orElse(null);
            userDto = userMapper.userToUserDto(user);
            //初始化所有子部门的id和no
            userDto.setOrganizationNoList(generateOrganizationNoList(user).stream().collect(Collectors.toList()));
            userDto.setOrganizationIdList(generateOrganizationIdList(user).stream().collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            childOrganizationThread.remove();
            countThread.remove();
        }
        return userDto;
    }

    private Set<Long> generateOrganizationIdList(User user) {
        Set<Organization> userHadOrganizations = user.getOrganizations();
        Set<Long> orgIdSet = new HashSet<>();
        List<Organization> organizationList = organizationService.findAllOrganizationList();
        countThread.set(0);
        //recursiveCount = 0;
        for (Organization org : userHadOrganizations) {
            treeOrganizationList(organizationList, user.getOrganizations().size(), org.getId());
            Set<Long> idSet = childOrganizationThread.get().stream().map(organization -> organization.getId()).collect(Collectors.toSet());
            orgIdSet.addAll(idSet);
        }
        return orgIdSet;
    }


    private Set<String> generateOrganizationNoList(User user) {
        Set<Organization> userHadOrganizations = user.getOrganizations();
        Set<String> orgNoSet = new HashSet<>();
        List<Organization> organizationList = organizationService.findAllOrganizationList();
        countThread.set(0);
        //recursiveCount = 0;
        for (Organization org : userHadOrganizations) {
            treeOrganizationList(organizationList, user.getOrganizations().size(), org.getId());
            Set<String> idSet = childOrganizationThread.get().stream().map(organization -> organization.getOrganizationNo()).collect(Collectors.toSet());
            orgNoSet.addAll(idSet);
        }
        return orgNoSet;
    }

    /**
     * 查找该部门下所有子节点
     *
     * @param organizationList
     * @param parentId
     * @return
     */
    private List<Organization> treeOrganizationList(List<Organization> organizationList, Integer orgSetCount, Long parentId) {
        //recursiveCount += 1;
        countThread.set(countThread.get() + 1);
        if (countThread.get() > (organizationList.size() * orgSetCount)) {
            throw new OperationException(SysadminError.organizationHasInfiniteRecursion, new RuntimeException(), "OrganizationId=" + parentId);
        }
        for (Organization organization : organizationList) {
            if (organization.getId().equals(parentId)) {
                //没有父级,添加自己
                childOrganizationThread.get().add(organization);
            } else if (null != organization.getParent() && organization.getParent().getId().equals(parentId)) {
                treeOrganizationList(organizationList, orgSetCount, organization.getId());
                childOrganizationThread.get().add(organization);
            }
        }
        return childOrganizationThread.get();
    }

    @Override
    public Set<String> findRoles(String username) {
        User user = findByUsername(username);
        if (user == null) {
            return Collections.EMPTY_SET;
        }
        return user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findPermissions(String username) {
        User user = findByUsername(username);
        if (user == null || user.getRoles() == null) {
            return Collections.EMPTY_SET;
        }
        return resourceDao.findRolePermission(user.getRoles().stream().map(Role::getId).collect(Collectors.toList())).stream().collect(Collectors.toSet());

//        return user.getRoles().stream().flatMap(rs -> rs.getResources().stream()).
//                filter(r -> StringUtils.isNoneBlank(r.getPermission()))
//                .map(Resource::getPermission).collect(Collectors.toSet());

    }

}
