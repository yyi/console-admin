package com.founder.console.web.controller.sysadmin.restful;

import com.founder.console.web.config.annotation.AjaxController;
import com.founder.contract.sysadmin.ResourceService;
import com.founder.domain.sysadmin.Resource;
import com.founder.dto.sysadmin.EmptyJsonResponse;
import com.founder.dto.sysadmin.ResourceDeleteDto;
import com.founder.dto.sysadmin.ResourceDto;
import com.founder.dto.sysadmin.mapping.ResourceMapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@AjaxController
@RequestMapping("/resource")
public class ResourceRestfulController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceMapper resourceMapper;

    @RequiresPermissions("resource:view")
    @RequestMapping("/list")
    public ResourceDto findResourceTree() {
        return resourceMapper.resourceToResourceDto(resourceService.findRootResource());
    }

    @RequiresPermissions("resource:create")
    @RequestMapping("/create")
    public ResourceDto createResource(@RequestBody ResourceDto resourceDto) {
        return resourceMapper.resourceToResourceDto(resourceService.createResource(resourceMapper.resourceDtoToResource(resourceDto), resourceDto.getParentId()));
    }

    @RequiresPermissions("resource:update")
    @RequestMapping("/update")
    public ResponseEntity updateResource(@RequestBody ResourceDto resourceDto) {
        resourceService.updateResource(resourceDto);
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequiresPermissions("resource:delete")
    @RequestMapping("/delete")
    public ResponseEntity deleteResource(@RequestBody ResourceDeleteDto resourceDeleteDto) {
        resourceService.deleteResource(resourceDeleteDto.getId(), resourceDeleteDto.isFocedDelete());
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }


}
