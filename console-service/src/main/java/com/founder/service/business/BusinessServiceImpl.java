package com.founder.service.business;

import com.founder.Exception.OperationException;
import com.founder.contract.business.BusinessService;
import com.founder.contract.business.QuestionsVersionService;
import com.founder.dao.business.*;
import com.founder.domain.BusinessType;
import com.founder.domain.business.*;
import com.founder.dto.business.*;
import com.founder.dto.sysadmin.mapping.*;
import com.founder.exception.business.BusinessError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by Administrator on 2017/12/27.
 */
@Service
@Transactional
public class BusinessServiceImpl implements BusinessService{

    @Autowired
    private BusinessDao businessDao;

    @Autowired
    private BusinessOperationDao businessOperationDao;


    @Autowired
    private BusinessKeyDao businessKeyDao;

    @Autowired
    private QuestionsDao questionsDao;

    @Autowired
    private BusinessKeyMapper businessKeyMapper;


    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private BusinessAuditMapper businessAuditMapper;

    @Autowired
    private BusinessValDao businessValDao;

    @Autowired
    private AnswerDao answerDao;


    @Autowired
    private ClientUserDao clientUserDao;
    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private QuestionsVersionService questionsVersionService;

    @Autowired
    private QuestionsVersionDao questionsVersionDao;

    @Autowired
    private BusinessAuditDao businessAuditDao;


    @Autowired
    private BusinessAuditDetailDao businessAuditDetailDao;



    @Override
    public List<Business> findAllBusinessByUser(ClientUser user) {


        user = clientUserDao.findById(user.getId()).orElse(null);

        List<Business.Status> statuss = new ArrayList<Business.Status>();
        statuss.add(Business.Status.UNFINISHED);
        statuss.add(Business.Status.COMPLETE);
        statuss.add(Business.Status.AUDIT_YES);
        statuss.add(Business.Status.AUDIT_NO);

        List<Business> businesses = businessDao.findAllByClientUserAndStatusInOrderByCreateDateDesc(user,statuss);

        return businesses;
    }

    @Override
    public Business create(Business business, ClientUser clientUser) {

        clientUser = clientUserDao.findById(clientUser.getId()).orElse(null);
        business.setCreateDate(new Date());
        business.setStatus(Business.Status.UNFINISHED);


        if(clientUser.isPersonal()){
            if(business.getServiceType()==Business.ServiceType.DEPT){
                throw new OperationException(BusinessError.BusinessServiceTypeError);
            }

        }


        business.setClientUser(clientUser);

        business = businessDao.save(business);


        BusinessOperation fillBusinessOperation  = new BusinessOperation();
        fillBusinessOperation.setOrders(1L);
        fillBusinessOperation.setBusiness(business);
        BusinessOperation questionBusinessOperation = new BusinessOperation();
        questionBusinessOperation.setOrders(2L);
        questionBusinessOperation.setBusiness(business);
        BusinessOperation fileBusinessOperation = new BusinessOperation();
        fileBusinessOperation.setOrders(3L);
        fileBusinessOperation.setBusiness(business);


        if(business.getServiceType()==Business.ServiceType.DEPT){
            business.setName("债券业务申请");
            if(!clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.DEPT_SPECIALTY){
                fillBusinessOperation.setBusinessType(BusinessType.FILL_DEPT_ORGANIZATION_SPECIALTY);
            }else if(!clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.DEPT_ORDINARY){
                fillBusinessOperation.setBusinessType(BusinessType.FILL_DEPT_ORGANIZATION_ORDINARY);
            }

            if(!clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.DEPT_ORDINARY){
                questionBusinessOperation.setBusinessType(BusinessType.QUESTIONS_DEPT_ORGANIZATION);
            }

            if(business.getInvestorType()==Business.InvestorType.DEPT_SPECIALTY){
                fileBusinessOperation.setBusinessType(BusinessType.FILE_DEPT_SPECIALTY);
            }else if(business.getInvestorType()==Business.InvestorType.DEPT_ORDINARY){
                fileBusinessOperation.setBusinessType(BusinessType.FILE_DEPT_ORDINARY);
            }


        }else if(business.getServiceType()==Business.ServiceType.SHARES){
            business.setName("股票业务申请");
            if(!clientUser.isPersonal()){
                fillBusinessOperation.setBusinessType(BusinessType.FILL_SHARES_ORGANIZATION);
            }else{
                fillBusinessOperation.setBusinessType(BusinessType.FILL_SHARES_EVERYMAN);
            }

            if(!clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.SHARES_ORDINARY){
                questionBusinessOperation.setBusinessType(BusinessType.QUESTIONS_SHARES_ORGANIZATION);
            }else if(clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.SHARES_ORDINARY){
                questionBusinessOperation.setBusinessType(BusinessType.QUESTIONS_SHARES_EVERYMAN);
            }

            if(!clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.SHARES_A_SPECIALTY){
                fileBusinessOperation.setBusinessType(BusinessType.FILE_SHARES_ORGANIZATION_A_SPECIALTY);
            }else if(!clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.SHARES_B_SPECIALTY){
                fileBusinessOperation.setBusinessType(BusinessType.FILE_SHARES_ORGANIZATION_B_SPECIALTY);
            }else if(!clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.SHARES_ORDINARY){
                fileBusinessOperation.setBusinessType(BusinessType.FILE_SHARES_ORGANIZATION_ORDINARY);
            }else if(clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.SHARES_SPECIALTY){
                fileBusinessOperation.setBusinessType(BusinessType.FILE_SHARES_EVERYMAN_SPECIALTY);
            }else if(clientUser.isPersonal()&&business.getInvestorType()==Business.InvestorType.SHARES_ORDINARY){
                fileBusinessOperation.setBusinessType(BusinessType.FILE_SHARES_EVERYMAN_ORDINARY);
            }


        }

        businessDao.save(business);

        if(fillBusinessOperation.getBusinessType()!=null){
            businessOperationDao.save(fillBusinessOperation);
        }
        if(questionBusinessOperation.getBusinessType()!=null){
            businessOperationDao.save(questionBusinessOperation);
        }
        if(fileBusinessOperation.getBusinessType()!=null){
            businessOperationDao.save(fileBusinessOperation);
        }

        return business;
    }

    @Override
    public BusinessValueDto findBusinessValueDtoById(Long businessId,ClientUser user) {

        BusinessValueDto businessValueDto = new BusinessValueDto();
        Business business = businessDao.findById(businessId).orElse(null);


        if(business==null){
            throw new OperationException(BusinessError.BusinessOrderIsExit);
        }
        user = clientUserDao.findById(user.getId()).orElse( null);
        List<Business> businesses = user.getBusinesss();
        if(!businesses.contains(business)){
            throw new OperationException(BusinessError.BusinessNotInUser);
        }

        BusinessDto businessDto = businessMapper.businessToBusinessDto(business);
        businessValueDto.setBusinessDto(businessDto);

        List<BusinessOperation> businessOperations = businessOperationDao.findAllByBusinessOrderByOrdersAsc(business);

        if(businessOperations==null&&businessOperations.isEmpty()){
            throw new OperationException(BusinessError.BusinessOrderOperationsIsExit);
        }

        BusinessAudit businessAudit = businessAuditDao.findFirstByBussinessAndStatusOrderByOperationTimeDesc(business,BusinessAudit.Status.AUDITED);

        if(businessAudit!=null){
            businessValueDto.setBusinessAuditDto(businessAuditMapper.businessAuditToBusinessAuditDto(businessAudit));
        }

        for (BusinessOperation businessOperation :businessOperations) {

            BusinessType businessType = businessOperation.getBusinessType();


            List<BusinessKey> businessKeys = businessKeyDao.findAllByBusinessTypeOrderByOrdersAsc(businessType);

            if(businessKeys!=null&&!businessKeys.isEmpty()){

                Map<String, List<ValueDto>> valueDtoMap = null;
                if(businessValueDto.getValueDtoMap()==null){
                    valueDtoMap = new HashMap<String, List<ValueDto>>();
                }else{
                    valueDtoMap = businessValueDto.getValueDtoMap();
                }

                for (BusinessKey key:businessKeys) {

                    BusinessVal val = businessValDao.findByBusinessAndAndBusinessKey(business,key);

                    List<ValueDto> list = new ArrayList<ValueDto>();
                    ValueDto dto = new ValueDto();
                    dto.setDisplayName(key.getDisplayName());
                    dto.setId(key.getId());
                    dto.setKey(key.getKey());
                    dto.setVal(val!=null&& StringUtils.isNotBlank(val.getVal())?val.getVal():"");

                    if(businessAudit!=null&&key.getKey().indexOf("FILE_")!=-1){
                        BusinessAuditDetail businessAuditDetail = businessAuditDetailDao.findByBusinessAuditAndKey(businessAudit,key.getKey());
                        if(businessAuditDetail!=null){
                            dto.setAuditResult(businessAuditDetail.getStatus().name());
                        }
                    }

                    list.add(dto);
                    valueDtoMap.put(key.getKey(),list);

                    businessValueDto.getKeyOrders().add(key.getKey());
                }
                businessValueDto.setValueDtoMap(valueDtoMap);
            }

            QuestionsVersion questionsVersion = questionsVersionService.findMaxVersionByBusinessTypeAndStatusAvailiable(businessType);


            if(questionsVersion!=null){

                List<Questions> questionss = questionsVersion.getQuestionss();

                Map<String, List<QuestionsDto>> questionsDtoMap = new HashMap<String, List<QuestionsDto>>();
                List<List<Long>> selectAnswers  = new ArrayList<List<Long>>();
                for (Questions questions:questionss) {

                    QuestionsDto dto = questionsMapper.questionsToQuestionsDto(questions);

                    for (Answer answer:questions.getAnswers()) {
                        List<Long> temp = new ArrayList<Long>();
                        if(business.getAnswers().contains(answer)){
                            //dto.getSelectAnswers().add(answerMapper.answerToAnswerDto(answer));
                            temp.add(answer.getId());
                        }
                        selectAnswers.add(temp);
                    }

                    if(!questionsDtoMap.containsKey(questions.getType().name())){
                        List<QuestionsDto> list = new ArrayList<QuestionsDto>();
                        list.add(dto);
                        questionsDtoMap.put(questions.getType().name(),list);
                    }else{
                        List<QuestionsDto> list = questionsDtoMap.get(questions.getType().name());
                        list.add(dto);
                        questionsDtoMap.put(questions.getType().name(),list);
                    }

                }

                businessValueDto.setSelectAnswers(selectAnswers);
                businessValueDto.setQuestionsDtoMap(questionsDtoMap);

            }



        }


        return businessValueDto;
    }

    @Override
    public Business update(BusinessValueDto businessValueDto,ClientUser user) {

        Business business = businessDao.findById(businessValueDto.getBusinessDto().getId()).orElse(null);

        if(business==null){
            throw new OperationException(BusinessError.BusinessOrderIsExit);
        }

        if(business.getStatus()!=Business.Status.UNFINISHED&&business.getStatus()!=Business.Status.AUDIT_NO){
            throw new OperationException(BusinessError.BusinessStatusError);
        }


        user = clientUserDao.findById(user.getId()).orElse(null);
        List<Business> businesses = user.getBusinesss();
        if(!businesses.contains(business)){
            throw new OperationException(BusinessError.BusinessNotInUser);
        }


        if(!businessValueDto.getValueDtoMap().isEmpty()){


            Map<String, List<ValueDto>> valueDtoMap = businessValueDto.getValueDtoMap();

            for (String key: valueDtoMap.keySet()) {
                List<ValueDto> list = valueDtoMap.get(key);

                for (ValueDto valueDto:list) {

                    if("ORGANIZATION_NAME".equals(valueDto.getKey())){
                        business.setOrganizationName(valueDto.getVal());
                    }else if("EMAIL".equals(valueDto.getKey())){
                        user.setEmailAddr(valueDto.getVal());
                    }else if("IPHONE".equals(valueDto.getKey())){
                        user.setMobileNo(valueDto.getVal());
                    }
                    BusinessKey businessKey = businessKeyDao.findById(valueDto.getId()).orElse(null);
                    BusinessVal businessVal = businessValDao.findByBusinessAndAndBusinessKey(business,businessKey);

                    BusinessVal val = null;
                    if(businessVal!=null){
                        val = businessVal;
                    }else{
                        val = new BusinessVal();
                        val.setBusiness(business);
                        val.setBusinessKey(businessKey);

                    }
                    val.setVal(valueDto.getVal());
                    businessValDao.save(val);
                }

            }
            businessDao.save(business);
            clientUserDao.save(user);
        }



        if(businessValueDto.getSelectAnswers()!=null&&!businessValueDto.getSelectAnswers().isEmpty()){
            business.setAnswers(null);
            businessDao.save(business);


            List<List<Long>> selectAnswerIds = businessValueDto.getSelectAnswers();

            List<List<Answer>> selectAnswers = new ArrayList<List<Answer>>();
            for (List<Long> ids:selectAnswerIds) {
                List<Answer> temp = new ArrayList<Answer>();
                for (Long id:ids) {
                    Answer answer = answerDao.findById(id).orElse(null);
                    temp.add(answer);
                }
                selectAnswers.add(temp);
            }

            Long totleScope = 0l;
            for (List<Answer> answers:selectAnswers) {

                Collections.sort(answers,new AnswerComparator());


                for (Answer answer:answers ) {

                    if(business.getAnswers()==null){
                        Set<Answer> set = new HashSet<Answer>();
                        set.add(answer);
                        business.setAnswers(set);
                    }else{
                        business.getAnswers().add(answer);
                    }


                }

                if(answers!=null&&!answers.isEmpty()){
                    totleScope = totleScope +answers.get(0).getScore();
                    business.setTotalScope(totleScope);
                    business.setRiskLevel(this.evaluate(totleScope,BusinessType.valueOf(businessValueDto.getOperation())));
                }

            }
            businessDao.save(business);

        }

        return business;
    }


    @Override
    public void complete(Long businessId, ClientUser user) {

        Business business = businessDao.findById(businessId).orElse(null);

        if(business==null){
            throw new OperationException(BusinessError.BusinessOrderIsExit);
        }

        if(business.getStatus()!=Business.Status.UNFINISHED&&business.getStatus()!=Business.Status.AUDIT_NO){
            throw new OperationException(BusinessError.BusinessStatusError);
        }

        user = clientUserDao.findById(user.getId()).orElse(null);
        List<Business> businesses = user.getBusinesss();
        if(!businesses.contains(business)){
            throw new OperationException(BusinessError.BusinessNotInUser);
        }

        business.setStatus(Business.Status.COMPLETE);
        businessDao.save(business);

        BusinessAudit audit = new BusinessAudit();

        audit.setBussiness(business);
        audit.setStatus(BusinessAudit.Status.UNAUDITED);
        audit.setOperationStaff(user.getLoginName());
        audit.setOperationTime(new Date());
        businessAuditDao.save(audit);


    }

    @Override
    public void delete(Long businessId, ClientUser user) {

        Business business = businessDao.findById(businessId).orElse(null);

        if(business==null){
            throw new OperationException(BusinessError.BusinessOrderIsExit);
        }

        if(business.getStatus()!=Business.Status.UNFINISHED&&business.getStatus()!=Business.Status.AUDIT_NO){
            throw new OperationException(BusinessError.BusinessStatusError);
        }

        user = clientUserDao.findById(user.getId()).orElse(null);
        List<Business> businesses = user.getBusinesss();
        if(!businesses.contains(business)){
            throw new OperationException(BusinessError.BusinessNotInUser);
        }

        business.setStatus(Business.Status.DISABLE);
        businessDao.save(business);

    }


    public Business.RiskLevel evaluate(Long totalScope,BusinessType type){
        Business.RiskLevel riskLevel = null;
        if(type==BusinessType.QUESTIONS_SHARES_EVERYMAN){

            if(totalScope<=34){
                riskLevel = Business.RiskLevel.C1;
            }else if(totalScope>=35&&totalScope<=52){
                riskLevel = Business.RiskLevel.C2;
            }else if(totalScope>=53&&totalScope<=82){
                riskLevel = Business.RiskLevel.C3;
            }else if(totalScope>=83&&totalScope<=98){
                riskLevel = Business.RiskLevel.C4;
            }else if(totalScope>=99&&totalScope<=120){
                riskLevel = Business.RiskLevel.C5;
            }
        }

        if(type==BusinessType.QUESTIONS_SHARES_ORGANIZATION){

            if(totalScope<=42){
                riskLevel = Business.RiskLevel.C1;
            }else if(totalScope>=43&&totalScope<=66){
                riskLevel = Business.RiskLevel.C2;
            }else if(totalScope>=67&&totalScope<=88){
                riskLevel = Business.RiskLevel.C3;
            }else if(totalScope>=89&&totalScope<=100){
                riskLevel = Business.RiskLevel.C4;
            }else if(totalScope>=101&&totalScope<=120){
                riskLevel = Business.RiskLevel.C5;
            }
        }


        return riskLevel;
    }


}
