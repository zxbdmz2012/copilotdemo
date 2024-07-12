package com.github.copilot.task.model;

import com.github.copilot.task.enums.JobEnum;
import lombok.Data;

/**
 * Model representing the configuration for a cron job.
 * This model includes the scheduling details such as day of week, month, and time,
 * as well as the job type and intervals for execution.
 */
@Data
public class CronModel {

    Integer[] dayOfWeeks;
    Integer[] dayOfMonths;
    Integer[] months;
    Integer second;
    Integer minute;
    Integer hour;
    // The type of job, defined by the JobEnum.
    JobEnum jobType;
    // The interval between job executions.
    Integer beApart;
    // The rate at which the job should execute.
    Integer rateInt;
    // The cycle of job execution.
    Integer cycleInt;
}