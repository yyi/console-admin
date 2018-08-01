package com.founder.console.web.config;

import com.founder.Exception.OperationException;
import com.founder.console.web.config.annotation.AjaxControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;


@AjaxControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorItem> handle(Exception e) {
        log.error("系统异常", e);

        ErrorItem error = new ErrorItem();
        error.setCode("FFFFFF");
        error.setMessage(e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OperationException.class)
    public ResponseEntity<ErrorItem> handle(OperationException operationException) {
        log.error("业务异常", operationException);

        ErrorItem error = new ErrorItem();

        error.setCode(operationException.getErrorCode());
        error.setMessage(operationException.getErrorMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    public static class ErrorItem {
        private String code;
        private String message;


        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


}
