package com.founder.console.web.tag;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.LinkedHashSet;
import java.util.Set;

public class JExtDialect extends AbstractDialect implements IProcessorDialect {
    private static final String NAME = "JExt";
    private static final String PREFIX = "jext";

    public JExtDialect() {
        super(NAME);
    }


    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public int getDialectProcessorPrecedence() {
        return StandardDialect.PROCESSOR_PRECEDENCE;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {

        LinkedHashSet processors = new LinkedHashSet();

        processors.add(new CsrfElementProcessor(PREFIX));

        return processors;
    }
}
