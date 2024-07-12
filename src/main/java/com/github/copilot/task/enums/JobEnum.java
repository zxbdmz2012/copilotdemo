package com.github.copilot.task.enums;

/**
 * Enum for representing different job frequencies.
 * This enum defines various time intervals at which jobs can be scheduled.
 */
public enum JobEnum {

    EVERY("Every day", 0),
    DAY("Day", 1),
    MONTH("Month", 2),
    WEEK("Week", 3),
    YEAR("Year", 4);

    private final String name;
    private final Integer value;

    JobEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}