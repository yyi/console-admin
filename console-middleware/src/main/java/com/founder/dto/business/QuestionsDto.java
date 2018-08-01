package com.founder.dto.business;

import com.founder.domain.business.Questions;
import com.founder.dto.sysadmin.UserDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QuestionsDto {


    private Long id;

    private String title;

    private List<AnswerDto> answers;

    private List<AnswerDto> selectAnswers;

    private Long businessId;

    private String type;

    private String questionsType;

}
