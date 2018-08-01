package com.founder.console.web.controller;

import com.founder.console.web.config.RestExceptionHandler;
import com.founder.console.web.config.annotation.WebController;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.web.ErrorAttributes;
//import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import  org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@WebController
@Log4j2
public class WebErrorController implements ErrorController {

    /**
     * Error Attributes in the Application
     */
    @Autowired
    private ErrorAttributes errorAttributes;

    private final static String ERROR_PATH = "/error";


    @RequestMapping(
            value = {ERROR_PATH},
            produces = {MediaType.TEXT_HTML_VALUE}
    )
    public ModelAndView errorHtml(HttpServletRequest request) {
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, false);
        String errorPage = MapUtils.getIntValue(errorAttributes, "status", -1) == 404 ? "404" : "500";
        return new ModelAndView(errorPage, errorAttributes);
    }

    @RequestMapping(
            value = {ERROR_PATH},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<RestExceptionHandler.ErrorItem> errorRest(HttpServletRequest request) {
        RestExceptionHandler.ErrorItem error = new RestExceptionHandler.ErrorItem();
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, false);
        error.setCode("FFFFFF");
        error.setMessage("系统忙");
        return new ResponseEntity<>(error, HttpStatus.valueOf(MapUtils.getIntValue(errorAttributes, "status", HttpStatus.BAD_REQUEST.value())));
    }


    /**
     * Returns the path of the error page.
     *
     * @return the error path
     */
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


    private Map<String, Object> getErrorAttributes(HttpServletRequest request,
                                                   boolean includeStackTrace) {
        WebRequest requestAttributes = new ServletWebRequest(request);
        Map<String, Object> map = this.errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
        String URL = request.getRequestURL().toString();
        map.put("URL", URL);
        log.debug(() -> "WebErrorController.method [error info]: status-" + map.get("status") + ", request url-" + URL);
        return map;
    }

}


