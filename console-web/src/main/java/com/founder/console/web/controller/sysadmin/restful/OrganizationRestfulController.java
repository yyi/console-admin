package com.founder.console.web.controller.sysadmin.restful;

import com.founder.console.web.bind.annotation.CurrentUser;
import com.founder.console.web.config.annotation.AjaxController;
import com.founder.contract.sysadmin.OrganizationService;
import com.founder.domain.sysadmin.Organization;
import com.founder.domain.sysadmin.User;
import com.founder.dto.sysadmin.EmptyJsonResponse;
import com.founder.dto.sysadmin.OrganizationDto;
import com.founder.dto.sysadmin.OrganizationReqDto;
import com.founder.dto.sysadmin.mapping.OrganizationMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@AjaxController
@RequestMapping("/organization")
public class OrganizationRestfulController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationMapper organizationMapper;

    @RequiresPermissions("organization:list")
    @RequestMapping("/list")
    public OrganizationDto list(@RequestBody OrganizationReqDto organizationReqDto) {
        OrganizationDto organizationDto = organizationService.findRootOrganizationDto();
        if(organizationReqDto.getId() != null){
            organizationDto = organizationService.configOrganizationDto(organizationDto, organizationReqDto.getId());
        }
        return organizationDto;
    }

    @RequiresPermissions("organization:update")
    @RequestMapping("/updateParent")
    public ResponseEntity updateParent(@RequestBody  OrganizationReqDto organizationReqDto) {
        organizationService.updateParent(organizationReqDto);
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequiresPermissions("organization:create")
    @RequestMapping("/create")
    public OrganizationDto create(@RequestBody OrganizationDto organizationDto, @CurrentUser User loginUser) {
        Organization organization = organizationMapper.OrganizationDtoToOrganization(organizationDto);
        organization = organizationService.create(organization, organizationDto.getParentId(), loginUser.getLoginName());
        return organizationMapper.OrganizationToOrganizationDto(organization);
    }

    @RequiresPermissions("organization:update")
    @RequestMapping("/update")
    public ResponseEntity update(@RequestBody  OrganizationDto organizationDto) {
        organizationService.update(organizationDto);
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequiresPermissions("organization:delete")
    @RequestMapping("/delete")
    public ResponseEntity delete(@RequestBody OrganizationDto organizationDto) {
        organizationService.delete(organizationDto.getId());
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

}
