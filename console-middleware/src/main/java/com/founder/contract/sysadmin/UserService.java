package com.founder.contract.sysadmin;

import com.founder.domain.sysadmin.User;
import com.founder.dto.sysadmin.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface UserService {
    /**
     * 创建用户
     * @param user
     */
    public User createUser(User user, List<Long> roleIds, List<Long>organizationIds);

    public User updateUser(User user,List<Long> roleIds,List<Long>organizationIds);

    public void deleteUser(Long userId);

    /**
     * 修改密码
     * @param userId
     * @param newPassword
     */
    public void changePassword(Long userId, String newPassword);


    User findOne(Long userId);

    public Page<User> findAll(String loginName,String userName,Pageable pageRequest);

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findByUsername(String username);

    /**
     * 根据用户名查找其角色
     * @param username
     * @return
     */
    public Set<String> findRoles(String username);

    /**
     * 根据用户名查找其权限
     * @param username
     * @return
     */
    public Set<String> findPermissions(String username);

    UserDto converUserToUserDto(User user);
}
