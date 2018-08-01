package com.founder.console.web.utils;


import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
@Log4j2
public class WebUtils {

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return header != null && "XMLHttpRequest".equals(header);
    }

    public static boolean isHttpStatusSucessful(int status) {
        HttpStatus.Series series = HttpStatus.Series.valueOf(status);
        return HttpStatus.Series.SUCCESSFUL.equals(series);
    }

    public static boolean isAcceptJson(HttpServletRequest request) {
        return StringUtils.containsIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }

    public static HttpServletRequest getCurrentHttpRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request;
        }
        log.debug("Not called in the context of an HTTP request");
        return null;
    }

}
