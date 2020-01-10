package org.devgateway.importtool.services.processor.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMATTER = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = new ThreadLocal<>();

    public static String formatDate(Boolean isTimeStamp, Date date) {
        if (isTimeStamp) {
            return getDateTimeFormatter().format(date);
        } else {
            return getDateFormatter().format(date);
        }
    }

    public static Date parseDate(Boolean isTimeStamp, String date) throws ParseException {
        if (isTimeStamp) {
            return getDateTimeFormatter().parse(date);
        } else {
            return getDateFormatter().parse(date);
        }
    }

    private static SimpleDateFormat getDateTimeFormatter() {
        if (DATE_TIME_FORMATTER.get() == null) {
            SimpleDateFormat format = new SimpleDateFormat(Constants.ISO8601_DATE_AND_TIME_FORMAT);
            format.setLenient(false);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            DATE_TIME_FORMATTER.set(format);
        }

        return DATE_TIME_FORMATTER.get();
    }

    private static SimpleDateFormat getDateFormatter() {
        if (DATE_FORMATTER.get() == null) {
            SimpleDateFormat format = new SimpleDateFormat(Constants.ISO8601_DATE_FORMAT);
            format.setLenient(false);
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
