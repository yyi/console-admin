package com.founder.dto.business;

import lombok.Data;

import java.util.*;

/**
 * Created by Administrator on 2017/12/27.
 */
@Data
public class BusinessValueDto {


    private BusinessDto businessDto;

    private Map<String, List<ValueDto>> valueDtoMap;

    private Map<String, List<QuestionsDto>> questionsDtoMap;

    private List<List<Long>> selectAnswers;

    private List<String> keyOrders = new ArrayList<String>();

    private String operation;

    private Map<String,String> map = new HashMap<String,String>();

    private BusinessAuditDto businessAuditDto;


    public Map<String,Object> conversion(){

        Map<String,Object> map = new HashMap<String,Object>();
        if(valueDtoMap!=null&&!valueDtoMap.isEmpty()){
            for (String str:valueDtoMap.keySet()) {
                List<ValueDto> valueDto = valueDtoMap.get(str);

                map.put(str,valueDto.get(0).getVal());
            }

        }
        return map;
    }


}
