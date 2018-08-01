
package com.founder.console.web.captcha;

import com.octo.captcha.service.CaptchaServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Slf4j
public class CaptchaUtile {
    public static ImageCaptchaService captchaService;

    public static void init(ImageCaptchaService ics) {
        Assert.notNull(ics, "ImageCaptchaService may not be null!");
        captchaService = ics;
    }

    private static boolean assertCaptchaService() {
        if (captchaService == null) {
            throw new IllegalStateException("ImageCaptchaService must  initialize!");
        }
        return true;
    }

    public static boolean validateResponse(HttpServletRequest request, String userCaptchaResponse) {
        assertCaptchaService();
        if (request.getSession(false) == null) return false;

        boolean validated = false;
        try {
            String id = getCaptchaId(request.getSession());
            validated = captchaService.validateResponseForID(id, userCaptchaResponse).booleanValue();
        } catch (CaptchaServiceException e) {
            log.error("验证码验证异常", e);
        }
        return validated;
    }

    public static boolean hasCaptcha(HttpServletRequest request, String userCaptchaResponse) {
        assertCaptchaService();
        if (request.getSession(false) == null) return false;
        boolean validated = false;
        try {
            String id = getCaptchaId(request.getSession());
            validated = captchaService.hasCapcha(id, userCaptchaResponse);
        } catch (CaptchaServiceException e) {
            log.error("验证码验证异常", e);
        }
        return validated;
    }

    public static String getCaptchaId(HttpSession session) {
        String id = (String) session.getAttribute("CaptchaId");
        if (StringUtils.isBlank(id)) {
            id = session.getId() + UUID.randomUUID();
            session.setAttribute("CaptchaId", id);
        }
        return id;
    }


    public static String generateCaptchaId(HttpSession session) {
        String id = session.getId() + UUID.randomUUID();
        session.setAttribute("CaptchaId", id);
        return id;

    }

}
