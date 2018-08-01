package com.founder.console.web.controller;


import com.founder.Exception.OperationException;
import com.founder.console.web.bind.annotation.CurrentUser;
import com.founder.console.web.config.annotation.WebController;
import com.founder.contract.business.BusinessService;
import com.founder.contract.business.VerificationCodeService;
import com.founder.dao.business.BusinessDao;
import com.founder.dao.business.BusinessOperationDao;
import com.founder.domain.business.Business;
import com.founder.domain.business.BusinessOperation;
import com.founder.domain.business.ClientUser;
import com.founder.dto.sysadmin.EmptyJsonResponse;
import com.founder.exception.business.BusinessError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@WebController
@RequestMapping
public class BusinessController {

    @Autowired
    private BusinessDao businessDao;

    @Autowired
    private BusinessOperationDao businessOperationDao;

    @RequestMapping("/businessIndex")
    public String businessIndex(@CurrentUser ClientUser clientUser){

        return "";
    }

    @RequestMapping("/businessFill")
    public String businessFill(@CurrentUser ClientUser clientUser,Model model){

        model.addAttribute("isPersonal",clientUser.isPersonal());
        return "apply";
    }

    @RequestMapping("/detail")
    public String detail(@RequestParam(value = "businessId", required = true) Long businessId,Model model){

        model.addAttribute("businessId",businessId);
        return "detail";
    }

    @RequestMapping("/next")
    public String next(@RequestParam(value = "businessId", required = true) Long businessId,
                       @RequestParam(value = "operation", required = true) String operation,Model model){

        Business business = businessDao.findById(businessId).orElse(null);

        if(business==null){
            throw new OperationException(BusinessError.BusinessOrderIsExit);
        }

        List<BusinessOperation> businessOperations =  businessOperationDao.findAllByBusinessOrderByOrdersAsc(business);


        if(businessOperations==null||businessOperations.isEmpty()){
            throw new OperationException(BusinessError.BusinessOrderOperationsIsExit);
        }

        if(StringUtils.isBlank(operation)){

            operation = businessOperations.get(0).getBusinessType().name();

            if(operation.indexOf("QUESTIONS_")!=-1){
                model.addAttribute("isQuestions",true);
            }else{
                model.addAttribute("isQuestions",false);
            }
        }else{

            if(operation.indexOf("QUESTIONS_")!=-1){
                model.addAttribute("isQuestions",true);
            }else{
                model.addAttribute("isQuestions",false);
            }

            for (int i = businessOperations.size()-1;i>=0;i--){
                BusinessOperation businessOperation = businessOperations.get(i);
                if(operation.equals(businessOperation.getBusinessType().name())){
                    if(i==businessOperations.size()-1){
                        operation = businessOperation.getBusinessType().name();
                    }else{
                        operation = businessOperations.get(i+1).getBusinessType().name();
                    }
                    break;
                }
            }
        }



        model.addAttribute("operation",operation);
        model.addAttribute("businessId",businessId);
        if(operation.indexOf("FILE_")!=-1){
            return "upload";
        }else if(operation.indexOf("QUESTIONS_")!=-1){
            return "questionnaire";
        }else if(operation.indexOf("FILL_")!=-1){
            return "baseInfo";
        }else{
            return "404";
        }
    }


    @RequestMapping("/back")
    public String back(@RequestParam(value = "businessId", required = true) Long businessId,
                       @RequestParam(value = "operation", required = true) String operation,Model model){

        Business business = businessDao.findById(businessId).orElse(null);

        if(business==null){
            throw new OperationException(BusinessError.BusinessOrderIsExit);
        }

        List<BusinessOperation> businessOperations =  businessOperationDao.findAllByBusinessOrderByOrdersAsc(business);

        if(businessOperations==null||businessOperations.isEmpty()){
            throw new OperationException(BusinessError.BusinessOrderOperationsIsExit);
        }

        if(StringUtils.isBlank(operation)){

            operation = businessOperations.get(0).getBusinessType().name();

        }else{

            for (int i = 0;i<=businessOperations.size()-1;i++){
                BusinessOperation businessOperation = businessOperations.get(i);
                if(operation.equals(businessOperation.getBusinessType().name())){
                    if(i==0){
                        operation = businessOperation.getBusinessType().name();
                    }else{
                        operation = businessOperations.get(i-1).getBusinessType().name();
                    }
                    break;
                }
            }
        }



        model.addAttribute("businessId",businessId);
        model.addAttribute("operation",operation);
        if(operation.indexOf("FILE_")!=-1){
            return "upload";
        }else if(operation.indexOf("QUESTIONS_")!=-1){
            return "questionnaire";
        }else if(operation.indexOf("FILL_")!=-1){
            return "baseInfo";
        }else{
            return "404";
        }
    }

}
