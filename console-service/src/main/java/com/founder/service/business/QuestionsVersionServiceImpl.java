package com.founder.service.business;

import com.founder.contract.business.QuestionsVersionService;
import com.founder.dao.business.QuestionsVersionDao;
import com.founder.domain.BusinessType;
import com.founder.domain.Status;
import com.founder.domain.business.QuestionsVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2018/1/30.
 */
@Service
@Transactional
public class QuestionsVersionServiceImpl implements QuestionsVersionService {

    @Autowired
    private QuestionsVersionDao questionsVersionDao;

    @Override
    public QuestionsVersion findMaxVersionByBusinessTypeAndStatusAvailiable(BusinessType businessType) {

        return questionsVersionDao.findFirstByBusinessTypeAndStatusOrderByIdDesc(businessType, Status.AVAILIABLE);
    }
}
