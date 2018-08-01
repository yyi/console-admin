package com.founder.console.web.view;

import org.apache.commons.io.IOUtils;
import org.thymeleaf.IEngineConfiguration;

import org.thymeleaf.context.AbstractContext;


import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

public class StringAndClassLoaderResourceResolver extends AbstractConfigurableTemplateResolver {


    public StringAndClassLoaderResourceResolver() {
        super();
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        Validate.notNull(resourceName, "Resource name cannot be null");

        return null;
    }


//    public String getName() {
//        return getClass().getName().toUpperCase();
//    }


//    public InputStream getResourceAsStream(final TemplateProcessingParameters params, final String resourceName) {
//        Validate.notNull(resourceName, "Resource name cannot be null");
//        if (StringContext.class.isAssignableFrom(params.getContext().getClass())) {
//            String content = ((StringContext) params.getContext()).getContent();
//            return IOUtils.toInputStream(content);
//        }
//        return ClassLoaderUtils.getClassLoader(ClassLoaderResourceResolver.class).getResourceAsStream(resourceName);
//    }

    public static class StringContext extends AbstractContext {

        private final String content;

        public StringContext(String content) {
            this.content = content;
        }

        public StringContext(String content, Locale locale) {
            super(locale);
            this.content = content;
        }

        public StringContext(String content, Locale locale, Map<String, Object>  variables) {
            super(locale, variables);
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}