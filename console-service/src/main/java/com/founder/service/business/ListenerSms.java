package com.founder.service.business;

import com.founder.contract.business.SmsService;
import com.founder.dto.business.EventSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
public class ListenerSms implements ApplicationListener<EventSms> {

    @Autowired
    private SmsService smsService;

    public void onApplicationEvent(EventSms eventSms) {
        smsService.sendSms(eventSms.getPhoneNo(), eventSms.getText());
    }
}
