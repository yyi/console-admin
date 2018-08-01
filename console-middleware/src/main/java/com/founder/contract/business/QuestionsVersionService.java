package com.founder.contract.business;

import com.founder.domain.BusinessType;
import com.founder.domain.business.Business;
import com.founder.domain.business.ClientUser;
import com.founder.domain.business.Questions;
import com.founder.domain.business.QuestionsVersion;
import com.founder.dto.business.BusinessValueDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface QuestionsVersionService {


    QuestionsVersion findMaxVersionByBusinessTypeAndStatusAvailiable(BusinessType businessType);


}
