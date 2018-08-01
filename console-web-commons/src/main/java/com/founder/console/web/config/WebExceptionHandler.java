package com.founder.console.web.config;

import com.founder.console.web.config.annotation.WebControllerAdvice;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@WebControllerAdvice
@Slf4j
public class WebExceptionHandler {

    @ExceptionHandler(Exception.class)
    @SneakyThrows
    public ModelAndView handleError(HttpServletRequest request, Exception e) {
        log.error("Server 500 error ,Request: " + request.getRequestURL(), e);
//        if (!WebUtils.isAjaxRequest(request))
//            return new ModelAndView("500");
        throw  e;
    }

    //not work
//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ModelAndView handleError404(HttpServletRequest request, Exception e) {
//        log.error("Server 404 error ,Request: {},Exception:{}", request.getRequestURL(), e);
//        return new ModelAndView("404");
//    }
}
