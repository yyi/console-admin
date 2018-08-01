package com.founder.domain.business;

import java.util.Comparator;

/**
 * Created by Administrator on 2018/1/3.
 */
public class AnswerComparator implements Comparator<Answer> {

    @Override
    public int compare(Answer o1, Answer o2) {
        return o2.getScore().intValue()-o1.getScore().intValue();
    }
}
