package com.founder.service.business;

import com.founder.contract.business.MailService;
import com.founder.dto.business.EventMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ListenerMail implements ApplicationListener<EventMail> {

    @Autowired
    private MailService mailService;

    @Async
    public void onApplicationEvent(EventMail eventMail) {
        mailService.sendMail(eventMail.getTargetMail(),eventMail.getSubject(),eventMail.getText());
    }
}
