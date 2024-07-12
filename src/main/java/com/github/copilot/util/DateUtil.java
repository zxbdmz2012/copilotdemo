package com.github.copilot.util;


import com.github.copilot.task.enums.TimeZoneEnum;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    /**
     * Converts a LocalDateTime object to a string representation.
     *
     * @param localDateTime The LocalDateTime object to format.
     * @return A string representation of the LocalDateTime, formatted according to the specified pattern.
     */
    public static String date2str(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null; // Or return a default value like "N/A"
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    /**
     * Formats a Date object into a string representation.
     *
     * @param date   The Date object to format.
     * @param format The string format to use.
     * @return A string representation of the date, formatted according to the specified format.
     */
    public static String format(Date date, String format) {
        if (date == null || format == null || format.isEmpty()) {
            throw new IllegalArgumentException("Date and format must not be null or empty");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static ZonedDateTime getzonedDateTime(LocalDateTime localDateTime, String timeZone) {
        ZoneId zoneId = null;
        if (StringUtil.isNotEmpty(timeZone)) {
            if (TimeZoneEnum.CST.getName().equals(timeZone)) {
                zoneId = ZoneId.of("America/Chicago");
            } else if (TimeZoneEnum.EST.getName().equals(timeZone)) {
                zoneId = ZoneId.of("America/New_York");
            } else if (TimeZoneEnum.MST.getName().equals(timeZone)) {
                zoneId = ZoneId.of("America/Denver");
            } else if (TimeZoneEnum.IST.getName().equals(timeZone)) {
                zoneId = ZoneId.of("Asia/Kolkata");
            } else if (TimeZoneEnum.GMT.getName().equals(timeZone)) {
                zoneId = ZoneId.of("GMT");
            } else if (TimeZoneEnum.UTC.getName().equals(timeZone)) {
                zoneId = ZoneId.of("UTC");
            } else if (TimeZoneEnum.PST.getName().equals(timeZone)) {
                zoneId = ZoneId.of("America/Los_Angeles");
            } else if (TimeZoneEnum.HST.getName().equals(timeZone)) {
                zoneId = ZoneId.of("Pacific/Honolulu");
            } else if (TimeZoneEnum.AEST.getName().equals(timeZone)) {
                zoneId = ZoneId.of("Australia/Sydney");
            } else {
                zoneId = ZoneId.of("Asia/Shanghai");
            }
        } else {
            zoneId = ZoneId.systemDefault();
        }
        return localDateTime.atZone(zoneId);
    }
}