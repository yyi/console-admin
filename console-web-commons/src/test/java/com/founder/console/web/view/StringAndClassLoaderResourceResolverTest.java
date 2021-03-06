package com.founder.console.web.view;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;


public class StringAndClassLoaderResourceResolverTest {
    private static SpringTemplateEngine templateEngine;

    @BeforeAll
    public static void setup(){
        StringTemplateResolver resolver = new StringTemplateResolver();
     //   resolver.setResourceResolver(new StringAndClassLoaderResourceResolver());
//        resolver.setPrefix("mail/"); // src/test/resources/mail
//        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
//        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setOrder(1);

        templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    @Test
    public void testStringResolution() {
        String expected = "<div>dave</div>";
        String input = "<div th:text=\"${userName}\">Some Username Here!</div>";
        AbstractContext context = new StringAndClassLoaderResourceResolver.StringContext(input);
        context.setVariable("userName", "dave");
        String actual = templateEngine.process(expected, context);
        Assertions.assertEquals(expected, actual);
    }

//    @Test
//    public void testClasspathResolution(){
//        AbstractContext context = new Context();
//        context.setVariable("message", "Hello Thymeleaf!");
//        String actual = templateEngine.process("dummy", context);
//        String expected = "<h1>Hello Thymeleaf!</h1>";
//        assertEquals(expected, actual);
//    }
}