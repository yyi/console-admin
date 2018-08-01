package com.founder.console.web.controller.restful;

import com.founder.console.web.bind.annotation.CurrentUser;
import com.founder.console.web.config.annotation.AjaxController;
import com.founder.contract.business.BusinessService;
import com.founder.domain.business.Business;
import com.founder.domain.business.ClientUser;
import com.founder.dto.business.BusinessDto;
import com.founder.dto.business.BusinessValueDto;
import com.founder.dto.sysadmin.EmptyJsonResponse;
import com.founder.dto.sysadmin.mapping.BusinessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/12/27.
 */
@AjaxController
@RequestMapping("/business")
public class BusinessRestfulController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessMapper businessMapper;

    @RequestMapping("/findAllBusinessByUser")
    public List<BusinessDto> findAllBusinessByUser(@CurrentUser ClientUser clientUser){

        return businessMapper.businesssToBusinessDtos(businessService.findAllBusinessByUser(clientUser));
    }


    @RequestMapping("/createBusiness")
    public BusinessDto create(@CurrentUser ClientUser clientUser, @RequestBody BusinessDto businessDto){
        Business business = businessService.create(businessMapper.BusinessDtoToBusiness(businessDto),clientUser);
        return businessMapper.businessToBusinessDto(business);
    }

    @RequestMapping("/updateBusiness")
    public BusinessDto update(@CurrentUser ClientUser clientUser,@RequestBody BusinessValueDto businessValueDto){

        Business business = businessService.update(businessValueDto,clientUser);
        return businessMapper.businessToBusinessDto(business);
    }

    @RequestMapping("/findBusinessValueDtoById")
    public BusinessValueDto findBusinessValueDtoById(@CurrentUser ClientUser clientUser,@RequestParam(value = "businessId", required = true)Long businessId){

        return businessService.findBusinessValueDtoById(businessId,clientUser);
    }

    @RequestMapping("/complete")
    public ResponseEntity complete(@CurrentUser ClientUser clientUser,@RequestParam(value = "businessId", required = true)Long businessId){


        businessService.complete(businessId,clientUser);

        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @RequestMapping("/delete")
    public ResponseEntity delete(@CurrentUser ClientUser clientUser,@RequestParam(value = "businessId", required = true)Long businessId){

        businessService.delete(businessId,clientUser);

        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }


}
