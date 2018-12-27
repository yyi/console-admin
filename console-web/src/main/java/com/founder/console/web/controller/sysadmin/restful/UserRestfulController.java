package com.founder.console.web.controller.sysadmin.restful;

import com.founder.console.web.config.annotation.AjaxController;
import com.founder.contract.sysadmin.UserService;
import com.founder.domain.sysadmin.User;
import com.founder.dto.sysadmin.EmptyJsonResponse;
import com.founder.dto.sysadmin.UserDto;
import com.founder.dto.sysadmin.mapping.UserMapper;
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
@RequestMapping("/user")
public class UserRestfulController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @RequiresPermissions("user:view")
    @RequestMapping("/list")
    public Page<UserDto> index(@RequestParam(value = "loginName", required = false) String loginName,
                               @RequestParam(value = "name", required = false) String name,
                               Pageable pageRequest) {
        final Page<User> all = userService.findAll(loginName,name,pageRequest);
        return new PageImpl(userMapper.usersToUserDtos(all.getContent()), pageRequest, all.getTotalElements());
    }

    @RequiresPermissions("user:create")
    @RequestMapping("/create")
    public UserDto createUser(@RequestBody
                                          UserDto userDto) {
        User user = userMapper.UserDtoToUser(userDto);
        user = userService.createUser(user,userDto.getRoleIds(),userDto.getOrganizationIds());
        return userMapper.userToUserDto(user);
    }

    @RequiresPermissions("user:update")
    @RequestMapping("/update")
    public void updateUser(@RequestBody UserDto userDto) {
        User user = userMapper.UserDtoToUser(userDto);
        userService.updateUser(user,userDto.getRoleIds(),userDto.getOrganizationIds());
   //     return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequiresPermissions("user:delete")
    @RequestMapping("/delete")
    public ResponseEntity deleteUser(@RequestBody UserDto userDto) {
        userService.deleteUser(userDto.getId());
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequestMapping("/changepassword")
    public ResponseEntity changePasswd(@RequestBody UserDto userDto) {
        userService.changePassword(userDto.getId(), userDto.getPasswd());
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }


}
