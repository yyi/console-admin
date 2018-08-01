package com.founder.dto.sysadmin.mapping;

import com.founder.domain.business.BusinessKey;
import com.founder.domain.business.Questions;
import com.founder.dto.business.QuestionsDto;
import com.founder.dto.business.ValueDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring" ,unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {AnswerMapper.class})
public interface QuestionsMapper {

    QuestionsDto questionsToQuestionsDto(Questions questions);

    List<QuestionsDto> questionssToQuestionsDtos(List<Questions> questionss);

    Questions questionsDtoToQuestions(QuestionsDto questionsDto);

}
