package com.founder.service.business;

import com.founder.dto.business.EventMail;
import com.founder.dto.business.EventSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class EventPbulish {

    @Autowired
    ApplicationContext context;

    /**
     * 发邮件
     * @param targetMail
     * @param subject
     * @param text
     */
    public void publishMail(String targetMail, String subject, String text) {

        context.publishEvent(new EventMail(this, targetMail,subject,text));
    }

    /**
     * 发短信
     * @param phoneNo
     * @param text
     */
    public void publishSms(String phoneNo, String text) {

        context.publishEvent(new EventSms(this,phoneNo,text));
    }

}
