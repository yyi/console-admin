package com.founder.service.business;

import com.founder.Exception.OperationException;
import com.founder.contract.business.MailService;
import com.founder.contract.sysadmin.DictionaryService;
import com.founder.exception.business.BusinessError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Transactional
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private DictionaryService dictionaryService;

    private JavaMailSenderImpl mailSender = null;

    public synchronized JavaMailSenderImpl getJavaMailSenderImpl() {
        // 邮箱配置
        if (mailSender == null) {
            mailSender = new JavaMailSenderImpl();
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", dictionaryService.getDictionaryMapByTypeAndDtKey("mail", "mailAuth"));
            properties.put("mail.smtp.timeout", dictionaryService.getDictionaryMapByTypeAndDtKey("mail", "mailTimeout"));
//            properties.put("mail.smtp.localhost", "email.foundersc.com");
//            properties.put("mail.mail.debug.localhost", "true");
            //  properties.put("mail.smtp.auth.mechanisms", "email.foundersc.com");
//            properties.put("mail.smtp.ssl.enable", dictionaryService.getDictionaryMapByTypeAndDtKey("mail", "sslEnable"));
//            properties.put("mail.smtp.socketFactory.class", dictionaryService.getDictionaryMapByTypeAndDtKey("mail","socketFactoryClass"));

            mailSender.setJavaMailProperties(properties);
            mailSender.setHost(dictionaryService.getDictionaryMapByTypeAndDtKey("mail", "mailServerHost"));
            mailSender.setPort(Integer.parseInt(dictionaryService.getDictionaryMapByTypeAndDtKey("mail", "mailServerPort")));
            mailSender.setUsername(dictionaryService.getDictionaryMapByTypeAndDtKey("mail", "mailSender"));
            mailSender.setPassword(dictionaryService.getDictionaryMapByTypeAndDtKey("mail", "mailPassword"));
        }
        return mailSender;
    }

    /**
     * 发送邮件
     *
     * @param targetMail 收件人
     * @param subject    邮件主题
     * @param text       邮件内容
     */
    @Override
    public void sendMail(String targetMail, String subject, String text) {
        mailSender = getJavaMailSenderImpl();
        MimeMessage msg = mailSender.createMimeMessage();
        log.info("targetMail:{},subject:{},text:{}",targetMail,subject,text);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            //helper.setFrom(mailSender.getUsername()); // 发送人
            helper.setFrom(dictionaryService.getDictionaryMapByTypeAndDtKey("mail", "mailFrom"));
            helper.setTo(targetMail);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            log.error("邮件发送失败",e);
            throw new OperationException(BusinessError.MailMessageError);
        }
        mailSender.send(msg);

    }

}
