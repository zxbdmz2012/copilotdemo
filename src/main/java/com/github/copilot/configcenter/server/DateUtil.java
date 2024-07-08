package com.github.copilot.configcenter.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DateUtil {

    private static final DateTimeFormatter FORMATTER_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String date2str1(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER_1);
    }
}
