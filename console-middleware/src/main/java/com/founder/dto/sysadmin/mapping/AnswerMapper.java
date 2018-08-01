package com.founder.dto.sysadmin.mapping;

import com.founder.domain.business.Answer;
import com.founder.domain.business.Questions;
import com.founder.dto.business.AnswerDto;
import com.founder.dto.business.QuestionsDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring" ,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerMapper {

    AnswerDto answerToAnswerDto(Answer answer);

    List<AnswerDto> answersToAnswerDtos(List<Answer> answers);


    Set<AnswerDto> answersToAnswerDtos(Set<Answer> answers);

    Answer answerDtoToAnswer(AnswerDto answerDto);

}
