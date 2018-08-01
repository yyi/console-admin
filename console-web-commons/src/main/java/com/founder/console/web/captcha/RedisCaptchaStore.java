package com.founder.console.web.captcha;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaAndLocale;
import com.octo.captcha.service.captchastore.CaptchaStore;
import org.apache.shiro.cache.Cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class RedisCaptchaStore implements CaptchaStore {

    private Cache cache;

    public RedisCaptchaStore( Cache cache) {
        this.cache = cache;
    }

    @Override
    public boolean hasCaptcha(String id) {
        return cache.get(id) != null;
    }

    @Override
    public void storeCaptcha(String id, Captcha captcha) throws CaptchaServiceException {
        cache.put(id, new CaptchaAndLocale(captcha));
    }

    @Override
    public void storeCaptcha(String id, Captcha captcha, Locale locale) throws CaptchaServiceException {
        cache.put(id, new CaptchaAndLocale(captcha, locale));
    }

    @Override
    public boolean removeCaptcha(String id) {
        cache.remove(id);
        return true;
    }

    private Object getCaptchaAndLocale(String id) {
        return cache.get(id);
    }

    @Override
    public Captcha getCaptcha(String id) throws CaptchaServiceException {
        Object captchaAndLocale = getCaptchaAndLocale(id);
        return captchaAndLocale != null ? ((CaptchaAndLocale) captchaAndLocale).getCaptcha() : null;
    }

    @Override
    public Locale getLocale(String id) throws CaptchaServiceException {
        Object captchaAndLocale = getCaptchaAndLocale(id);
        return captchaAndLocale != null ? ((CaptchaAndLocale) captchaAndLocale).getLocale() : null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public Collection getKeys() {
        return Collections.emptyList();
    }

    @Override
    public void empty() {
        cache.clear();
    }

    @Override
    public void initAndStart() {

    }

    @Override
    public void cleanAndShutdown() {
        empty();
    }
}
