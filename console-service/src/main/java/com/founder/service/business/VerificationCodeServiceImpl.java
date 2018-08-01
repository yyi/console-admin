package com.founder.service.business;

import com.founder.Exception.OperationException;
import com.founder.contract.business.ClientUserService;
import com.founder.contract.business.VerificationCodeService;
import com.founder.contract.sysadmin.DictionaryService;
import com.founder.dao.business.VerificationCodeDao;
import com.founder.domain.business.VerificationCode;
import com.founder.exception.business.BusinessError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private VerificationCodeDao verificationCodeDao;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private EventPbulish eventPbulish;

    @Autowired
    private ClientUserService clientUserService;

    /**
     * 生成验证码
     *
     * @param type
     * @param operationType
     * @param token
     */
    @Override
    public void create(String loginName, VerificationCode.Type type, VerificationCode.OperationType operationType, String token, String ip) {
        if (type == VerificationCode.Type.SMS) {
            if (!clientUserService.verifyMobileNo(loginName, token)) {
                throw new OperationException(BusinessError.RegisteredMailOrMobileError);
            }
        } else {
            if (!clientUserService.verifyEmail(loginName, token)) {
                throw new OperationException(BusinessError.RegisteredMailOrMobileError);
            }
        }

        //如果是短信，则校验当天和总共发送验证码次数，短信炸弹保护机制
        if (type == VerificationCode.Type.SMS) {
            this.checkSendCount(token);
        }
        //根据：令牌，类型，操作类型，状态，查出可用集合
        List<VerificationCode> verificationCodes = verificationCodeDao.getByTokenAndTypeAndOperationTypeAndStatusOrderByCreateTimeDesc(token, type,
                operationType, VerificationCode.Status.AVAILIABLE);
        //如果集合有数据,判断第一个有没有超时
        if (verificationCodes.size() > 0) {
            //当前时间-失效时间  小于0 ，没有失效，抛出异常
            if (System.currentTimeMillis() - verificationCodes.get(0).getInvaiidTime().getTime() < 0) {
                throw new OperationException(BusinessError.VerificationCodeNotDisabled);
            }
        }
        //生成验证码
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setToken(token);
        verificationCode.setCode(generateNo(6));
        verificationCode.setLoginName(loginName);
        verificationCode.setIp(ip);
        verificationCode.setStatus(VerificationCode.Status.AVAILIABLE);
        verificationCode.setCreateTime(new Date());
        verificationCode.setType(type);
        Calendar cal = Calendar.getInstance();
        cal.setTime(verificationCode.getCreateTime());
        cal.add(Calendar.SECOND, Integer.parseInt(dictionaryService.getDictionaryMapByTypeAndDtKey("VERIFICATION_CODE", "Timeout")));
        verificationCode.setInvaiidTime(cal.getTime());
        verificationCode.setOperationType(operationType);
        String text = "";

        log.info(verificationCode.toString());
        //调用发送短息
        if (type == VerificationCode.Type.SMS) {
            String content = dictionaryService.getDictionaryMapByTypeAndDtKey("VERIFICATION_CODE", "sms_content");
            //验证码替换配置内容的占位符
            text = MessageFormat.format(content,verificationCode.getCode());
            eventPbulish.publishSms(token, text);
        } else {
            String content = dictionaryService.getDictionaryMapByTypeAndDtKey("VERIFICATION_CODE", "mail_content");
            //验证码替换配置内容的占位符
            text = MessageFormat.format(content,verificationCode.getCode());
            String subject = dictionaryService.getDictionaryMapByTypeAndDtKey("VERIFICATION_CODE", "mail_subject");
            //调用发邮件的接口
            eventPbulish.publishMail(token, subject, text);
        }
        verificationCode.setContent(text);
        verificationCodeDao.save(verificationCode);

    }

    /**
     * 验证码校验
     *
     * @param token 令牌
     * @param code  验证码
     * @return
     */
    @Override
    public boolean verify(String token, String code) {
        //根据令牌和状态查找
        List<VerificationCode> verificationCodes = verificationCodeDao.getByTokenAndStatusOrderByCreateTimeDesc(token, VerificationCode.Status.AVAILIABLE);
        //存在记录，则判断是否有效
        if (verificationCodes.size() > 0) {
            //当前时间-失效时间  小于0 ，没有失效，校验通过
            if (System.currentTimeMillis() - verificationCodes.get(0).getInvaiidTime().getTime() < 0
                    && verificationCodes.get(0).getCode().equals(code)) {
                //改状态
                verificationCodes.stream().forEach(n -> n.setStatus(VerificationCode.Status.DISABLE));
                verificationCodeDao.saveAll(verificationCodes);
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    /**
     * 校验当天和总共发送验证码次数，短信炸弹保护机制
     *
     * @param token
     */
    private void checkSendCount(String token) {
        //获取当天（今日）零点零分零秒
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        int totalCount = verificationCodeDao.getAllByToken(token).size();
        //如果超过总限制次数，抛出异常
        if (totalCount > Integer.parseInt(dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "total_quota"))) {
            throw new OperationException(BusinessError.SmsMessageExceedTotalQuota);
        }
        int dayCount = verificationCodeDao.getAllByTokenAndCreateTimeAfter(token, zero).size();
        //如果超过当日限制次数，抛出异常
        if (dayCount > Integer.parseInt(dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "day_quota"))) {
            throw new OperationException(BusinessError.SmsMessageExceedDayQuota);
        }
    }

    /**
     * 生产随机数
     *
     * @param size
     * @return
     */
    public String generateNo(int size) {
        int random = (int) (Math.random() * Math.pow(10, size));
        return StringUtils.leftPad(random + "", size, "0");
    }
}
