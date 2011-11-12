package com.lmax.ticketing.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private static final ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>()
    {
        @Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        }
    };

    public String format(LogRecord logRecord)
    {
        String exception = (logRecord.getThrown() != null) ? exceptionToString(logRecord) : "";
        return "[" + dateFormatter.get().format(new Date(logRecord.getMillis())) + "] " +
            logRecord.getLoggerName() + " - " +
            logRecord.getLevel() + ": " +
            logRecord.getMessage() + "\n" +
            exception;
    }

    public String exceptionToString(LogRecord logRecord)
    {
        StringWriter writer = new StringWriter();
        logRecord.getThrown().printStackTrace(new PrintWriter(writer));
        return writer.toString() + "\n";
    }
}