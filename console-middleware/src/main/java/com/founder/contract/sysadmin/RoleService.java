package com.founder.contract.sysadmin;

import com.founder.domain.sysadmin.Resource;
import com.founder.domain.sysadmin.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    Set<Role> findRolesByIds(Iterable<Long> ids);

    public Role createRole(Role role);
    public Role updateRole(Role role);
    public void deleteRole(Long roleId);

    void updateAssociatedResource(Long roleId, List<Long> resourceIds);

    Set<Resource> findResourcesByRoleId(Long roleId);

    public Role findOne(Long roleId);
    public List<Role> findAllAvailible();


}
