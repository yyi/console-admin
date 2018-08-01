package com.founder.dto.commons;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class TemplateElementQueryResponseDto {

    private Map<Long, String> availableElement = Collections.emptyMap();

    private List<OptionDto> associatedElement = Collections.emptyList();

}
