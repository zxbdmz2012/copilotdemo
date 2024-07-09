package com.github.copilot.schedule.enums;
/**
 * Enum for representing different time zones.
 * This enum can be used to specify the time zone context for scheduling tasks.
 */
public enum TimeZoneEnum {
    EST("EST"),
    CST("CST"),
    MST("MST"),
    IST("IST"),
    GMT("GMT"),
    UTC("UTC"),
    PST("PST"),
    HST("HST"),
    AEST("AEST");

    private final String name;

    TimeZoneEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
