package com.founder.console.web.controller.business.restful;

import com.founder.console.web.config.annotation.AjaxController;
import com.founder.contract.business.ClientUserService;
import com.founder.domain.business.ClientUser;
import com.founder.dto.business.ClientUserDto;
import com.founder.dto.business.mapping.ClientUserMapper;
import com.founder.dto.sysadmin.EmptyJsonResponse;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AjaxController
@RequestMapping("/clientUser")
public class ClientUserRestfulController {

    @Autowired
    private ClientUserService clientUserService;

    @Autowired
    private ClientUserMapper clientUserMapper;

    @RequiresPermissions("clientUser:view")
    @RequestMapping("/list")
    public Page<ClientUserDto> index(@RequestParam(value = "loginName", required = false) String loginName,
                                     @RequestParam(value = "name", required = false) String name,
                                     Pageable pageRequest) {
        final Page<ClientUser> all = clientUserService.findAll(loginName,name,pageRequest);
        return new PageImpl(clientUserMapper.clientUsersToClientUserDtos(all.getContent()), pageRequest, all.getTotalElements());
    }

    @RequiresPermissions("clientUser:create")
    @RequestMapping("/create")
    public ClientUserDto createUser(@RequestBody ClientUserDto clientUserDto) {
        ClientUser clientUser = clientUserMapper.clientUserDtoToClientUser(clientUserDto);
        clientUser = clientUserService.createClientUser(clientUser);
        return clientUserMapper.clientUserToClientUserDto(clientUser);
    }

    @RequiresPermissions("clientUser:update")
    @RequestMapping("/update")
    public ResponseEntity updateUser(@RequestBody ClientUserDto clientUserDto) {
        ClientUser clientUser = clientUserMapper.clientUserDtoToClientUser(clientUserDto);
        clientUserService.updateClientUser(clientUser);
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequiresPermissions("clientUser:delete")
    @RequestMapping("/delete")
    public ResponseEntity deleteClientUser(@RequestBody ClientUserDto clientUserDto) {
        clientUserService.deleteClientUser(clientUserDto.getId());
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequestMapping("/changepassword")
    public ResponseEntity changePasswd(@RequestBody ClientUserDto clientUserDto) {
        clientUserService.changePasswordById(clientUserDto.getId(), clientUserDto.getPasswd());
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }


}
