package com.founder.dto.business;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckedItem {

    private  String text;

    private  boolean checked;
}
