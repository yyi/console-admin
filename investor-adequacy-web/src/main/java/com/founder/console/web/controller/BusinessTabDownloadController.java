package com.founder.console.web.controller;


import com.founder.console.web.bind.annotation.CurrentUser;
import com.founder.console.web.config.annotation.WebController;
import com.founder.console.web.utils.OfficeDocumentGeneratorUtil;
import com.founder.console.web.view.DownloadingView;
import com.founder.contract.business.BusinessService;
import com.founder.contract.sysadmin.DictionaryService;
import com.founder.domain.business.ClientUser;
import com.founder.dto.business.BusinessValueDto;
import com.founder.dto.business.CheckedItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import java.util.*;

@WebController
@Slf4j
@RequestMapping("/tab")
public class BusinessTabDownloadController {


    @Autowired
    OfficeDocumentGeneratorUtil officeDocumentGeneratorUtil;

    @Autowired
    BusinessService businessService;


    @Autowired
    private DictionaryService dictionaryService;


    //附件1 债券专业机构
    @RequestMapping(
            value = "/deptOrganizationSpecialtyBase",
            method = RequestMethod.GET
    )
    public View deptOrganizationSpecialtyBase(@CurrentUser ClientUser clientUser,@RequestParam(value = "businessId", required = true) Long businessId){
        BusinessValueDto businessValueDto = businessService.findBusinessValueDtoById(businessId,clientUser);
        Map<String,Object> map = new HashMap<String,Object>();
        if(businessValueDto.getValueDtoMap()!=null){
            map = businessValueDto.conversion();

            List<CheckedItem> occpupyCheckedItems = new ArrayList<>();
            occpupyCheckedItems.add(new CheckedItem("经有关金融监管部门批准设立的金融机构，包括证券公司、期货公司、基金管理公司及其子公司、商业银行、保险公司、信托公司、财务公司等；经行业协会备案或者登记的证券公司子公司、期货公司子公司、私募基金管理人。", map.get("ORGANIZATION_TYPE").toString().equals("经有关金融监管部门批准设立的金融机构，包括证券公司、期货公司、基金管理公司及其子公司、商业银行、保险公司、信托公司、财务公司等；经行业协会备案或者登记的证券公司子公司、期货公司子公司、私募基金管理人。")));
            occpupyCheckedItems.add(new CheckedItem("上述机构面向投资者发行的理财产品，包括但不限于证券公司资产管理产品、基金管理公司及其子公司产品、期货公司资产管理产品、银行理财产品、保险产品、信托产品、经行业协会备案的私募基金。", map.get("ORGANIZATION_TYPE").toString().equals("上述机构面向投资者发行的理财产品，包括但不限于证券公司资产管理产品、基金管理公司及其子公司产品、期货公司资产管理产品、银行理财产品、保险产品、信托产品、经行业协会备案的私募基金。")));
            occpupyCheckedItems.add(new CheckedItem("社会保障基金、企业年金等养老基金，慈善基金等社会公益基金，合格境外机构投资者（QFII）、人民币合格境外机构投资者（RQFII）。", map.get("ORGANIZATION_TYPE").toString().equals("社会保障基金、企业年金等养老基金，慈善基金等社会公益基金，合格境外机构投资者（QFII）、人民币合格境外机构投资者（RQFII）。")));
            map.put("occupyCheckItems",occpupyCheckedItems);

            List<CheckedItem> assetCheckItems = new ArrayList<>();
            assetCheckItems.add(new CheckedItem("1亿以下", map.get("SCALE").toString().equals("1亿以下")));
            assetCheckItems.add(new CheckedItem("1-5亿元", map.get("SCALE").toString().equals("1-5亿")));
            assetCheckItems.add(new CheckedItem("5-10亿元", map.get("SCALE").toString().equals("5-10亿")));
            assetCheckItems.add(new CheckedItem("10-20亿元", map.get("SCALE").toString().equals("10-20亿")));
            assetCheckItems.add(new CheckedItem("20-50亿元", map.get("SCALE").toString().equals("20-50亿")));
            assetCheckItems.add(new CheckedItem("50-100亿元", map.get("SCALE").toString().equals("50-100亿")));
            assetCheckItems.add(new CheckedItem("100亿元以上", map.get("SCALE").toString().equals("100亿元以上")));

            map.put("assetCheckItems",assetCheckItems);


            List<CheckedItem> badCredibilityCheckItems = new ArrayList<>();
            badCredibilityCheckItems.add(new CheckedItem("中国人民银行征信中心", false));
            badCredibilityCheckItems.add(new CheckedItem("最高人民法院失信被执行人名单", false));
            badCredibilityCheckItems.add(new CheckedItem("工商行政管理机构", false));
            badCredibilityCheckItems.add(new CheckedItem("税务管理机构", false));
            badCredibilityCheckItems.add(new CheckedItem("监管机构、自律组织", false));
            badCredibilityCheckItems.add(new CheckedItem("投资者在证券经营机构的失信记录", false));
            badCredibilityCheckItems.add(new CheckedItem("其他组织", false));
            badCredibilityCheckItems.add(new CheckedItem("无", false));
            badCredibilityCheckItems.add(new CheckedItem("有", false));

            if(StringUtils.isNotBlank(map.get("CREDIT_RECORD").toString())){
                for (String credit:map.get("CREDIT_RECORD").toString().split("-")) {
                    for (CheckedItem checkedItem:badCredibilityCheckItems) {
                        if(credit.equals(checkedItem.getText())){
                            checkedItem.setChecked(true);
                            break;
                        }
                    }

                }

            }
            map.put("badCredibilityCheckItems",badCredibilityCheckItems);

        }

        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");

        String fileName = UUID.randomUUID().toString();
        byte[] buff = officeDocumentGeneratorUtil.createPdf("investorBasicInformation_Specialty", map, uploadFilePath + fileName);
        return new DownloadingView("投资者基本信息表（机构）.pdf",
                "application/pdf", buff);
    }

    //附件4 股票机构
    //附件机构
    @RequestMapping(
            value = "/deptOrganizationBase",
            method = RequestMethod.GET
    )
    public View deptOrganizationBase(@CurrentUser ClientUser clientUser,@RequestParam(value = "businessId", required = true) Long businessId){
        BusinessValueDto businessValueDto = businessService.findBusinessValueDtoById(businessId,clientUser);
        Map<String,Object> map = new HashMap<String,Object>();
        if(businessValueDto.getValueDtoMap()!=null){
            map = businessValueDto.conversion();

            List<CheckedItem> occpupyCheckedItems = new ArrayList<>();
            occpupyCheckedItems.add(new CheckedItem("一般企业法人", map.get("ORGANIZATION_TYPE").toString().equals(">_<一般企业法人")));
            occpupyCheckedItems.add(new CheckedItem("金融机构", map.get("ORGANIZATION_TYPE").toString().equals(">_<金融机构")));
            occpupyCheckedItems.add(new CheckedItem("社会公益基金", map.get("ORGANIZATION_TYPE").toString().equals(">_<社会公益基金")));
            occpupyCheckedItems.add(new CheckedItem("QFII", map.get("ORGANIZATION_TYPE").toString().equals(">_<QFII")));
            occpupyCheckedItems.add(new CheckedItem("RQFII", map.get("ORGANIZATION_TYPE").toString().equals(">_<RQFII")));

            map.put("occupyCheckItems",occpupyCheckedItems);
            if(map.get("ORGANIZATION_TYPE").toString().indexOf(">_<")==-1){
                occpupyCheckedItems.add(new CheckedItem("其他，", true));
                map.put("occupyOther", map.get("ORGANIZATION_TYPE").toString());
            }
            map.put("occupyCheckItems",occpupyCheckedItems);

            List<CheckedItem> badCredibilityCheckItems = new ArrayList<>();
            badCredibilityCheckItems.add(new CheckedItem("中国人民银行征信中心", false));
            badCredibilityCheckItems.add(new CheckedItem("最高人民法院失信被执行人名单", false));
            badCredibilityCheckItems.add(new CheckedItem("工商行政管理机构", false));
            badCredibilityCheckItems.add(new CheckedItem("税务管理机构", false));
            badCredibilityCheckItems.add(new CheckedItem("监管机构、自律组织", false));
            badCredibilityCheckItems.add(new CheckedItem("投资者在证券经营机构的失信记录", false));
            badCredibilityCheckItems.add(new CheckedItem("其他组织", false));
            badCredibilityCheckItems.add(new CheckedItem("无", false));
            badCredibilityCheckItems.add(new CheckedItem("有", false));

            if(StringUtils.isNotBlank(map.get("CREDIT_RECORD").toString())){
                for (String credit:map.get("CREDIT_RECORD").toString().split("-")) {
                    for (CheckedItem checkedItem:badCredibilityCheckItems) {
                        if(credit.equals(checkedItem.getText())){
                            checkedItem.setChecked(true);
                            break;
                        }
                    }

                }

            }
            map.put("badCredibilityCheckItems",badCredibilityCheckItems);

        }

        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");

        String fileName = UUID.randomUUID().toString();
        byte[] buff = officeDocumentGeneratorUtil.createPdf("investorBasicInformation_Org", map, uploadFilePath + fileName);
        return new DownloadingView("投资者基本信息表（机构）.pdf",
                "application/pdf", buff);
    }


    //附件5 股票自然人
    @RequestMapping(
            value = "/shareEverymanBase",
            method = RequestMethod.GET
    )
    public View shareEverymanBase(@CurrentUser ClientUser clientUser,@RequestParam(value = "businessId", required = true) Long businessId){
        BusinessValueDto businessValueDto = businessService.findBusinessValueDtoById(businessId,clientUser);
        Map<String,Object> map = new HashMap<String,Object>();
        if(businessValueDto.getValueDtoMap()!=null){
            map = businessValueDto.conversion();


            List<CheckedItem> occpupyCheckedItems = new ArrayList<>();
            occpupyCheckedItems.add(new CheckedItem("党政机关工作人员", map.get("OCCUPATION").toString().equals(">_<党政机关工作人员")));
            occpupyCheckedItems.add(new CheckedItem("企事业单位职工", map.get("OCCUPATION").toString().equals(">_<企事业单位职工")));
            occpupyCheckedItems.add(new CheckedItem("农民", map.get("OCCUPATION").toString().equals(">_<农民")));
            occpupyCheckedItems.add(new CheckedItem("个体工商户", map.get("OCCUPATION").toString().equals(">_<个体工商户")));
            occpupyCheckedItems.add(new CheckedItem("注册会计师", map.get("OCCUPATION").toString().equals(">_<注册会计师")));
            occpupyCheckedItems.add(new CheckedItem("律师", map.get("OCCUPATION").toString().equals(">_<律师")));
            occpupyCheckedItems.add(new CheckedItem("学生", map.get("OCCUPATION").toString().equals(">_<学生")));
            occpupyCheckedItems.add(new CheckedItem("金融机构从业人员", map.get("OCCUPATION").toString().equals(">_<金融机构从业人员")));
            occpupyCheckedItems.add(new CheckedItem("金融机构高级管理人员", map.get("OCCUPATION").toString().equals(">_<金融机构高级管理人员")));
            occpupyCheckedItems.add(new CheckedItem("无业", map.get("OCCUPATION").toString().equals(">_<无业")));
            map.put("occupyCheckItems",occpupyCheckedItems);

            map.put("occupyCheckItems",occpupyCheckedItems);
            if(map.get("OCCUPATION").toString().indexOf(">_<")==-1){
                occpupyCheckedItems.add(new CheckedItem("其他，", true));
                map.put("occupyOther", map.get("OCCUPATION").toString());
            }



            List<CheckedItem> eductionCheckedItems = new ArrayList<>();
            eductionCheckedItems.add(new CheckedItem("博士", map.get("EDUCATION").toString().equals("博士")));
            eductionCheckedItems.add(new CheckedItem("硕士", map.get("EDUCATION").toString().equals("硕士")));
            eductionCheckedItems.add(new CheckedItem("本科", map.get("EDUCATION").toString().equals("本科")));
            eductionCheckedItems.add(new CheckedItem("大专", map.get("EDUCATION").toString().equals("大专")));
            eductionCheckedItems.add(new CheckedItem("中专", map.get("EDUCATION").toString().equals("中专")));
            eductionCheckedItems.add(new CheckedItem("高中", map.get("EDUCATION").toString().equals("高中")));
            eductionCheckedItems.add(new CheckedItem("初中及以下", map.get("EDUCATION").toString().equals("初中及以下")));

            map.put("educationCheckItems",eductionCheckedItems);



            List<CheckedItem> creditCheckedItems = new ArrayList<>();
            creditCheckedItems.add(new CheckedItem("中国人民银行征信中心", false));
            creditCheckedItems.add(new CheckedItem("最高人民法院失信被执行人名单", false));
            creditCheckedItems.add(new CheckedItem("工商行政管理机构", false));
            creditCheckedItems.add(new CheckedItem("税务管理机构", false));
            creditCheckedItems.add(new CheckedItem("监管机构、自律组织", false));
            creditCheckedItems.add(new CheckedItem("投资者在证券经营机构的失信记录", false));
            creditCheckedItems.add(new CheckedItem("其他组织", false));
            creditCheckedItems.add(new CheckedItem("无", false));
            creditCheckedItems.add(new CheckedItem("有", false));

            if(StringUtils.isNotBlank(map.get("CREDIT_RECORD").toString())){
                for (String credit:map.get("CREDIT_RECORD").toString().split("-")) {
                    for (CheckedItem checkedItem:creditCheckedItems) {
                        if(credit.equals(checkedItem.getText())){
                            checkedItem.setChecked(true);
                            break;
                        }
                    }

                }

            }
            map.put("creditCheckedItems",creditCheckedItems);
        }

        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");
        String fileName = UUID.randomUUID().toString();
        byte[] buff = officeDocumentGeneratorUtil.createPdf("investorBasicInformation", map, uploadFilePath + fileName);
        return new DownloadingView("投资者基本信息表（自然人）.pdf",
                "application/pdf", buff);
    }

    //附件2 债券 普通机构
    @RequestMapping(
            value = "/deptOrganizationOrdinaryBase",
            method = RequestMethod.GET
    )
    public View deptOrganizationOrdinaryBase(@CurrentUser ClientUser clientUser,@RequestParam(value = "businessId", required = true) Long businessId){

        BusinessValueDto businessValueDto = businessService.findBusinessValueDtoById(businessId,clientUser);
        Map<String,Object> map = new HashMap<String,Object>();
        if(businessValueDto.getValueDtoMap()!=null){
            map = businessValueDto.conversion();


            List<CheckedItem> occpupyCheckedItems = new ArrayList<>();
            occpupyCheckedItems.add(new CheckedItem("一般企业法人", map.get("ORGANIZATION_TYPE").toString().equals(">_<一般企业法人")));
            map.put("occupyCheckItems",occpupyCheckedItems);
            if(map.get("ORGANIZATION_TYPE").toString().indexOf(">_<")==-1){
                occpupyCheckedItems.add(new CheckedItem("其他，", true));
                map.put("occupyOther", map.get("ORGANIZATION_TYPE").toString());
            }



            List<CheckedItem> isOrganizationTypeCheckedItems = new ArrayList<>();

            isOrganizationTypeCheckedItems.add(new CheckedItem("最近一年末净资产不低于2000万元", false));
            isOrganizationTypeCheckedItems.add(new CheckedItem("最近一年末金融资产不低于1000万元", false));
            isOrganizationTypeCheckedItems.add(new CheckedItem("具有2年以上证券、基金、期货、黄金、外汇等投资经历", false));


            if(StringUtils.isNotBlank(map.get("IS_ORGANIZATION_TYPE").toString())){
                for (String credit:map.get("IS_ORGANIZATION_TYPE").toString().split("-")) {
                    for (CheckedItem checkedItem:isOrganizationTypeCheckedItems) {
                        if(credit.equals(checkedItem.getText())){
                            checkedItem.setChecked(true);
                            break;
                        }
                    }

                }

            }
            map.put("isOrganizationTypeCheckedItems",isOrganizationTypeCheckedItems);


            List<CheckedItem> creditCheckedItems = new ArrayList<>();

            creditCheckedItems.add(new CheckedItem("中国人民银行征信中心", false));
            creditCheckedItems.add(new CheckedItem("最高人民法院失信被执行人名单", false));
            creditCheckedItems.add(new CheckedItem("工商行政管理机构", false));
            creditCheckedItems.add(new CheckedItem("税务管理机构", false));
            creditCheckedItems.add(new CheckedItem("监管机构、自律组织", false));
            creditCheckedItems.add(new CheckedItem("投资者在证券经营机构的失信记录", false));
            creditCheckedItems.add(new CheckedItem("其他组织", false));
            creditCheckedItems.add(new CheckedItem("无", false));
            creditCheckedItems.add(new CheckedItem("有", false));

            if(StringUtils.isNotBlank(map.get("CREDIT_RECORD").toString())){
                for (String credit:map.get("CREDIT_RECORD").toString().split("-")) {
                    for (CheckedItem checkedItem:creditCheckedItems) {
                        if(credit.equals(checkedItem.getText())){
                            checkedItem.setChecked(true);
                            break;
                        }
                    }

                }

            }
            map.put("creditCheckedItems",creditCheckedItems);

        }


        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");

        String fileName = UUID.randomUUID().toString();
        byte[] buff = officeDocumentGeneratorUtil.createPdf("deptOrganizationOrdinaryBase", map, uploadFilePath + fileName);
        return new DownloadingView("投资者基本信息表（机构）.pdf",
                "application/pdf", buff);
    }

    //附件9 股票 专业投资者申请书
    @RequestMapping(
            value = "/shareSpecialtyApply",
            method = RequestMethod.GET
    )
    public View shareSpecialtyApply(){
        Map<String,Object> map = new HashMap<String,Object>();
        String fileName = UUID.randomUUID().toString();
        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");
        byte[] buff = officeDocumentGeneratorUtil.createPdf("shareSpecialtyApply", map, uploadFilePath + fileName);
        return new DownloadingView("专业投资者申请书.pdf",
                "application/pdf", buff);
    }




    }
