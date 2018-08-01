package com.founder.service.sysadmin;

import com.founder.Exception.OperationException;
import com.founder.contract.sysadmin.ResourceService;
import com.founder.dao.sysadmin.ResourceDao;
import com.founder.domain.Status;
import com.founder.domain.sysadmin.Resource;
import com.founder.domain.sysadmin.ResourceValue;
import com.founder.dto.sysadmin.ResourceDto;
import com.founder.exception.sysadmin.SysadminError;
import com.founder.form.sysadmin.Menu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    ResourceDao resourceDao;

    @Transactional(readOnly = true)
    @Override
    public List<Menu> getUserResourceByRoleIds(List<Long> roleid) {
        if (CollectionUtils.isEmpty(roleid)) return null;
        List<Menu> menuInfos = new ArrayList<>();
        List<ResourceValue> resourceValues = resourceDao.findRoleResource(roleid);
        resourceValues.forEach(rv -> {
            Menu menuInfo = getMenu(rv);
            if (rv.getLevel() == 1) {
                menuInfos.add(menuInfo);
            } else {
                Menu menu = getParentMenu(rv.getParentId(), menuInfos);
                if (menu == null)
                    log.error("菜单项未找到父节点，{}", rv);
                else
                    menu.appendSubMenu(menuInfo);
            }

        });
        return menuInfos;
    }

    private Menu getMenu(ResourceValue rv) {
        Menu menuInfo = new Menu();
        BeanUtils.copyProperties(rv, menuInfo);
        return menuInfo;
    }

    private Menu getParentMenu(Long parentId, List<Menu> menuInfos) {
        for (Menu menu : menuInfos) {
            if (menu.getId().equals(parentId))
                return menu;
            else {
                Menu mif = getParentMenu(parentId, menu.getSubMenus());
                if (mif != null) return mif;
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Resource findRootResource() {
        return findResourceById(1L);
    }

    private Resource findResourceById(Long id) {
        Assert.notNull(id, "资源id不能为空");
        return resourceDao.findById(id).orElse(null);
    }

    @Override
    public Resource createResource(Resource resource, Long parentId) {
        Resource parent = loadResourceById(parentId);
        resource.setParent(parent);
        resource.setId(null);
        resource.setStatus(Status.AVAILIABLE);
        resourceDao.save(resource);

        return resource;
    }

    @Override
    public Resource loadResourceById(Long parentId) {
        Resource parent = resourceDao.findById(parentId).orElse(null);
        if (!Objects.nonNull(parent))
            throw new OperationException(SysadminError.ResourceNotExists);
        return parent;
    }

    @Override
    public void updateResource(ResourceDto resourceDto) {
        Resource resource = loadResourceById(resourceDto.getId());
        resource.setName(resourceDto.getName());
        resource.setUrl(resourceDto.getUrl());
        resource.setPermission(resourceDto.getPermission());

    }

    @Override
    public void deleteResource(Long resourceId, boolean forcedDelete) {
        Resource resource = loadResourceById(resourceId);
        resourceDeleteVerify(forcedDelete, resource);
        resourceDao.delete(resource);
    }

    private void resourceDeleteVerify(boolean forcedDelete, Resource resource) {
        if (resource.hasSubResource() && !forcedDelete) {
            throw new OperationException(SysadminError.ResourceHasSubResource);
        }
        if (resource.hasAssociatedRole())
            throw new OperationException(SysadminError.ResourceHasSAssociatedRole);
    }

}
