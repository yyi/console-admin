package com.founder.dto.business;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class EventSms extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private String phoneNo;

    private String text;

    public EventSms(Object source, String phoneNo, String text) {
        super(source);
        this.phoneNo = phoneNo;
        this.text = text;
    }

}
