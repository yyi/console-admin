package com.founder.contract.sysadmin;

import com.founder.domain.sysadmin.Resource;
import com.founder.dto.sysadmin.ResourceDto;
import com.founder.form.sysadmin.Menu;

import java.util.List;

public interface ResourceService {
    List<Menu> getUserResourceByRoleIds(List<Long>  roleid);

    Resource findRootResource();

    Resource createResource(Resource resource, Long parentId);

    Resource loadResourceById(Long parentId);

    void updateResource(ResourceDto resourceDto);

    void deleteResource(Long resourceId, boolean forcedDelete);
}
