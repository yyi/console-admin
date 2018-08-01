package com.founder.console.web.config.annotation;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
public @interface AjaxController {
    String value() default "";
}
