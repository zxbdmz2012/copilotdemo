package com.github.copilot.task.utils;

import com.github.copilot.task.model.CronModel;
import com.github.copilot.util.ArrayUtil;
import java.util.*;

public class CronUtil {

    // Create a cron expression based on the given CronModel
    public static String createCronExpression(CronModel cronModel) {
        StringBuilder cronExp = new StringBuilder();

        // If rate and cycle are not null, create a loop cron expression
        if (Objects.nonNull(cronModel.getRateInt()) && Objects.nonNull(cronModel.getCycleInt())) {
            return createLoopCronExpression(cronModel.getRateInt(), cronModel.getCycleInt());
        }
        if (null == cronModel.getJobType()) {
            System.out.println("Execution cycle not configured");
        }

        // If second, minute, and hour are not null, build the cron expression
        if (null != cronModel.getSecond()
                && null != cronModel.getMinute()
                && null != cronModel.getHour()) {
            // Append seconds
            cronExp.append(cronModel.getSecond()).append(" ");
            // Append minutes
            cronExp.append(cronModel.getMinute()).append(" ");
            // Append hours
            cronExp.append(cronModel.getHour()).append(" ");
            // Daily job type
            if (cronModel.getJobType().getValue() == 1) {
                // If beApart is not null, append day and month
                if (cronModel.getBeApart() != null) {
                    cronExp.append("1");
                    cronExp.append("/");
                    cronExp.append(cronModel.getBeApart() + 1);
                    cronExp.append(" ");
                    cronExp.append("* ");
                    cronExp.append("?");
                } else {
                    // Append day, month, and week
                    cronExp.append("* ");
                    cronExp.append("* ");
                    cronExp.append("?");
                }
            }
            // Weekly job type
            else if (cronModel.getJobType().getValue() == 3) {
                // Append week
                cronExp.append("? ");
                cronExp.append("* ");
                Integer[] weeks = cronModel.getDayOfWeeks();
                for (int i = 0; i < weeks.length; i++) {
                    if (i == 0) {
                        cronExp.append(weeks[i]);
                    } else {
                        cronExp.append(",").append(weeks[i]);
                    }
                }
            }
            // Monthly job type
            else if (cronModel.getJobType().getValue() == 2) {
                // Append days of the month
                Integer[] days = cronModel.getDayOfMonths();
                if(Objects.isNull(days)) {
                    return "0 0 0 L * ?";
                }
                for (int i = 0; i < days.length; i++) {
                    if (i == 0) {
                        if (days[i] == 32) {
                            // Last day of the month
                            return "0 0 0 L * ?";
                        } else {
                            cronExp.append(days[i]);
                        }
                    } else {
                        cronExp.append(",").append(days[i]);
                    }
                }
                // Append month and week
                cronExp.append(" * ");
                cronExp.append("?");
            }
            // Yearly job type
            else if (cronModel.getJobType().getValue() == 4) {
                // Append days of the year
                Integer[] days = cronModel.getDayOfMonths();
                if (ArrayUtil.isEmpty(days)) {
                    cronExp.append("*");
                } else {
                    for (int i = 0; i < days.length; i++) {
                        if (i == 0) {
                            cronExp.append(days[i]);
                        } else {
                            cronExp.append(",").append(days[i]);
                        }
                    }
                }
                // Append months
                Integer[] months = cronModel.getMonths();
                if (ArrayUtil.isEmpty(months)) {
                    cronExp.append(" *");
                } else {
                    for (int i = 0; i < months.length; i++) {
                        Integer month = months[i];
                        if (month > 12) {
                            throw new RuntimeException("Month data exception: " + Arrays.toString(months));
                        }
                        if (i == 0) {
                            cronExp.append(" ").append(month);
                        } else {
                            cronExp.append(",").append(month);
                        }
                    }
                }
                cronExp.append(" ?");
            } else if (cronModel.getJobType().getValue() == 0) {
                // Append day, month, and week
                cronExp.append("* ");
                cronExp.append("* ");
                cronExp.append("?");
            }
        }
        return cronExp.toString();
    }

    // Build a loop cron expression based on rate and cycle
    public static String createLoopCronExpression(int rate, int cycle) {
        String cron = "";
        switch (rate) {
            case 0: // Execute every cycle seconds
                cron = "0/" + cycle + " * * * * ?";
                break;
            case 1: // Execute every cycle minutes
                cron = "0 0/" + cycle + " * * * ?";
                break;
            case 2: // Execute every cycle hours
                cron = "0 0 0/" + cycle + " * * ?";
                break;
            case 3: // Execute every cycle days at 0:00
                cron = "0 0 0 1/" + cycle + " * ?";
                break;
            case 4: // Execute every cycle months on the 1st at 0:00
                cron = "0 0 0 1 1/" + cycle + " ? ";
                break;
            case 5: // Execute every day at cycle hour
                cron = "0 0 " + cycle + "  * * ?";
                break;
            default: // Default to execute every cycle seconds
                cron = "0/1 * * * * ?";
                break;
        }
        return cron;
    }
}