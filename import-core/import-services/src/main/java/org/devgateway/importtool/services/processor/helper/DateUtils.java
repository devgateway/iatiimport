package org.devgateway.importtool.services.processor.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateUtils {
    
    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMATTER = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = new ThreadLocal<>();
    
    public static SimpleDateFormat getDateTimeFormatter() {
        if (DATE_TIME_FORMATTER.get() == null) {
            SimpleDateFormat format = new SimpleDateFormat(Constants.ISO8601_DATE_AND_TIME_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            DATE_TIME_FORMATTER.set(format);
        }
        
        return DATE_TIME_FORMATTER.get();
    }
    
    public static SimpleDateFormat getDateFormatter() {
        if (DATE_FORMATTER.get() == null) {
            SimpleDateFormat format = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            DATE_FORMATTER.set(format);
        }
        
        return DATE_FORMATTER.get();
    }
    
    public static boolean isValidDate(String dateString) {
        try {
            return getDateFormatter().parse(dateString) != null;
        } catch (ParseException e) {
            return false;
        }
    }
}
