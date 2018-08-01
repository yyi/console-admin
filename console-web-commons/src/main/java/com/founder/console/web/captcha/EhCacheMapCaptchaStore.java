package com.founder.console.web.captcha;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaAndLocale;
import com.octo.captcha.service.captchastore.CaptchaStore;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.util.Collection;
import java.util.Locale;


public class EhCacheMapCaptchaStore implements CaptchaStore {

    private Ehcache ehcache;

    public EhCacheMapCaptchaStore(Ehcache ehcache) {
        this.ehcache = ehcache;
    }

    @Override
    public boolean hasCaptcha(String id) {
        return ehcache.get(id) != null;
    }

    @Override
    public void storeCaptcha(String id, Captcha captcha) throws CaptchaServiceException {
        ehcache.put(new Element(id, new CaptchaAndLocale(captcha)));
    }

    @Override
    public void storeCaptcha(String id, Captcha captcha, Locale locale) throws CaptchaServiceException {
        ehcache.put(new Element(id, new CaptchaAndLocale(captcha, locale)));
    }

    @Override
    public boolean removeCaptcha(String id) {
        return ehcache.remove(id);
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

    private Object getCaptchaAndLocale(String id) {
        Element element = ehcache.get(id);
        return element != null ? element.getValue() : null;
    }

    @Override
    public int getSize() {
        return ehcache.getSize();
    }

    @Override
    public Collection getKeys() {
        return ehcache.getKeys();
    }

    @Override
    public void empty() {
        ehcache.removeAll();
    }

    @Override
    public void initAndStart() {

    }

    @Override
    public void cleanAndShutdown() {
        empty();
    }
}
