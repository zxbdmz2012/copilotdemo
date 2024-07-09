package com.github.copilot.schedule.enums;


public enum JobEnum {

    EVERY("每天", 0),
    DAY("日", 1),
    MONTH("月", 2),
    WEEK("周", 3),
    YEAR("年", 4),
    ;

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

