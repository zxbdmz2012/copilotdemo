package com.github.copilot.schedule.enums;

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
