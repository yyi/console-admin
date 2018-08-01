package com.founder.console.web.controller.sysadmin.restful;

import com.founder.console.web.config.annotation.AjaxController;
import com.founder.contract.sysadmin.RoleService;
import com.founder.dto.sysadmin.EmptyJsonResponse;
import com.founder.dto.sysadmin.ResourceDto;
import com.founder.dto.sysadmin.RoleDto;
import com.founder.dto.sysadmin.mapping.ResourceMapper;
import com.founder.dto.sysadmin.mapping.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@AjaxController
@RequestMapping("/role")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleRestfulController {

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    private  final ResourceMapper resourceMapper;

    @RequiresPermissions("role:view")
    @RequestMapping("/list")
    public List<RoleDto> list() {
        return roleMapper.rolesToRoleDtos(roleService.findAllAvailible());
    }

    @RequiresPermissions("role:create")
    @RequestMapping("/create")
    public RoleDto create(@RequestBody RoleDto roleDto) {
        return roleMapper.roleToRoleDto(roleService.createRole(roleMapper.RoleDtoToRole(roleDto)));
    }

    @RequiresPermissions("role:update")
    @RequestMapping("/update")
    public ResponseEntity update(@RequestBody RoleDto roleDto) {
        roleService.updateRole(roleMapper.RoleDtoToRole(roleDto));
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequiresPermissions("role:delete")
    @RequestMapping("/delete")
    public ResponseEntity delete(@RequestBody RoleDto roleDto) {
        roleService.deleteRole(roleDto.getId());
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequiresPermissions("role:resource:update")
    @RequestMapping("/resource/update")
    public ResponseEntity updateRoleResources(@RequestBody RoleDto roleDto) {
        roleService.updateAssociatedResource(roleDto.getId(),roleDto.getResourceIds());
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequiresPermissions("role:resource:list")
    @RequestMapping("/resource/list")
    public Set<ResourceDto> listRoleResources(@RequestBody RoleDto roleDto) {
        return resourceMapper.resourcesToResourceDos(roleService.findResourcesByRoleId(roleDto.getId()));
    }

}
