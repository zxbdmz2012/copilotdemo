package com.github.copilot.schedule.model;

import com.github.copilot.schedule.enums.JobEnum;
import lombok.Data;

@Data
public class CronModel {

    Integer[] dayOfWeeks;

    Integer[] dayOfMonths;

    Integer[] months;

    Integer second;

    Integer minute;

    Integer hour;

    /**
     * 类型
     */
    JobEnum jobType;
    /**
     * 间隔
     */
    Integer beApart;

    Integer rateInt;

    Integer cycleInt;


}