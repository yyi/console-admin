package com.founder.service.sysadmin;

import com.founder.Exception.OperationException;
import com.founder.contract.sysadmin.ResourceService;
import com.founder.contract.sysadmin.RoleService;
import com.founder.dao.sysadmin.RoleDao;
import com.founder.domain.Status;
import com.founder.domain.sysadmin.Resource;
import com.founder.domain.sysadmin.Role;
import com.founder.exception.sysadmin.SysadminError;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ResourceService resourceService;

    @Override
    public Set<Role> findRolesByIds(Iterable<Long> ids) {
        return roleDao.findAllById(ids).stream().collect(Collectors.toSet());
    }

    @Override
    public Role createRole(Role role) {
        List<Role> roles = roleDao.findByName(role.getName());
        if(roles.size() > 0){
            throw new OperationException(SysadminError.RoleNameExists);
        }
        role.setId(null);
        role.setStatus(Status.AVAILIABLE);
        return roleDao.save(role);
    }

    @Override
    public Role updateRole(Role role) {
        List<Role> roles = roleDao.findByName(role.getName());
        for (Role rl : roles) {
            if(!rl.getId().equals(role.getId())){
                throw new OperationException(SysadminError.RoleNameExists);
            }
        }

        Role r = findOne(role.getId());
        r.setName(role.getName());
        r.setDescription(role.getDescription());
        return r;
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = findOne(roleId);
        if(role.hasAssociatedUser())
            throw new OperationException(SysadminError.RoleHasAssociatedUser);
        roleDao.deleteById(roleId);
    }

    @Override
    public void updateAssociatedResource(Long roleId, List<Long> resourceIds){
        Role role = findOne(roleId);
        role.clearResources();
        resourceIds.stream().map(resourceService::loadResourceById).forEach(role::appendResource);
    }

    @Override
    public Set<Resource> findResourcesByRoleId(Long roleId) {
        return findOne(roleId).getResources();
    }

    @Override
    public Role findOne(Long roleId) {
        Role role = roleDao.findById(roleId).orElse(null);
        if(!ObjectUtils.allNotNull(role))
            throw new OperationException(SysadminError.RolesNotExists);
        return role;
    }

    @Override
    public List<Role> findAllAvailible() {
        return roleDao.findByStatus(Status.AVAILIABLE);
    }

}
