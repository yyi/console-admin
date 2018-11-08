package com.founder.log;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

@Plugin(name = "LogIdPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "y", "logId" })
public class LogIdPatternConverter extends LogEventPatternConverter {

    private static final LogIdPatternConverter INSTANCE =
            new LogIdPatternConverter();

    public static LogIdPatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    private LogIdPatternConverter(){
        super("LogId", "logId");
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        toAppendTo.append(Math.random());
    }

}