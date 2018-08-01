package com.founder.dto.business;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class EventMail extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private String targetMail;

    private String subject;

    private String text;

    public EventMail(Object source, String targetMail, String subject, String text) {
        super(source);
        this.targetMail = targetMail;
        this.subject = subject;
        this.text = text;

    }

}
