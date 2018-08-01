package com.founder.console.web.controller.business.restful;


import com.founder.console.web.config.annotation.AjaxController;
import com.founder.contract.business.VerificationCodeService;
import com.founder.domain.business.VerificationCode;
import com.founder.dto.business.VerificationCodeReqDto;
import com.founder.dto.sysadmin.EmptyJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@AjaxController
@RequestMapping("/VerificationCode")
public class VerificationCodeRestfulController {

    @Autowired
    private VerificationCodeService verificationCodeService;


    /**
     * 获取验证码
     *
     * @param verificationCodeReqDto
     * @return
     */
    @RequestMapping("/create")
    public ResponseEntity create(@RequestBody VerificationCodeReqDto verificationCodeReqDto, HttpServletRequest httpServletRequest) {

        verificationCodeService.create(verificationCodeReqDto.getLoginName(), VerificationCode.Type.valueOf(verificationCodeReqDto.getType()),
                VerificationCode.OperationType.valueOf(verificationCodeReqDto.getOperationType()), verificationCodeReqDto.getToken(), httpServletRequest.getHeader("X-Real-IP"));
        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

}
