package com.github.copilot.task.utils;

import com.github.copilot.task.enums.JobEnum;
import com.github.copilot.task.model.CronModel;
import com.github.copilot.util.ArrayUtil;
import com.github.copilot.util.DateUtil;
import com.github.copilot.util.StringUtil;
import org.quartz.CronTrigger;
import org.quartz.TriggerUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.text.ParseException;
import java.util.*;


public class CronUtil {





    public static String createCronExpression(CronModel cronModel) {
        StringBuilder cronExp = new StringBuilder();

        if(Objects.nonNull(cronModel.getRateInt())&&Objects.nonNull(cronModel.getCycleInt())){
            return createLoopCronExpression(cronModel.getRateInt(),cronModel.getCycleInt());
        }
        if (null == cronModel.getJobType()) {
            System.out.println("执行周期未配置");//执行周期未配置
        }

        if (null != cronModel.getSecond()
                && null != cronModel.getMinute()
                && null != cronModel.getHour()) {
            //秒
            cronExp.append(cronModel.getSecond()).append(" ");
            //分
            cronExp.append(cronModel.getMinute()).append(" ");
            //小时
            cronExp.append(cronModel.getHour()).append(" ");
            //每天
            if (cronModel.getJobType().getValue() == 1) {
                //12 12 12 1/2 * ? *
                //12 12 12 * * ?
                if (cronModel.getBeApart() != null) {
                    cronExp.append("1");//日
                    cronExp.append("/");
                    cronExp.append(cronModel.getBeApart() + 1);//月
                    cronExp.append(" ");
                    cronExp.append("* ");
                    cronExp.append("?");
                } else {
                    cronExp.append("* ");//日
                    cronExp.append("* ");//月
                    cronExp.append("?");//周
                }
            }

            //按每周
            else if (cronModel.getJobType().getValue() == 3) {
                //一个月中第几天
                cronExp.append("? ");
                //月份
                cronExp.append("* ");
                //周
                Integer[] weeks = cronModel.getDayOfWeeks();
                for (int i = 0; i < weeks.length; i++) {
                    if (i == 0) {
                        cronExp.append(weeks[i]);
                    } else {
                        cronExp.append(",").append(weeks[i]);
                    }
                }

            }
            //按每月
            else if (cronModel.getJobType().getValue() == 2) {
                //一个月中的哪几天
                Integer[] days = cronModel.getDayOfMonths();
                for (int i = 0; i < days.length; i++) {
                    if (i == 0) {
                        if (days[i] == 32) {
                            //本月最后一天
                            String endMouthCron = "0 0 0 L * ?";
                            return endMouthCron;
                        } else {
                            cronExp.append(days[i]);
                        }
                    } else {
                        cronExp.append(",").append(days[i]);
                    }
                }
                //月份
                cronExp.append(" * ");
                //周
                cronExp.append("?");
            }
            //按每年
            else if (cronModel.getJobType().getValue() == 4) {
                //一个年中的哪几天
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
                //月份
                Integer[] months = cronModel.getMonths();
                if (ArrayUtil.isEmpty(months)) {
                    cronExp.append(" *");
                } else {
                    for (int i = 0; i < months.length; i++) {
                        Integer month = months[i];
                        if (month > 12) {
                            throw new RuntimeException("月份数据异常: " + Arrays.toString(months));
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
                cronExp.append("* ");//日
                cronExp.append("* ");//月
                cronExp.append("?");//周
            }
        }
        return cronExp.toString();
    }

    /**
     * 生成计划的详细描述
     *
     * @param cronModel
     * @return String
     */
    public static String createDescription(CronModel cronModel) {
        StringBuffer description = new StringBuffer();
        //计划执行开始时间
//      Date startTime = cronModel.getScheduleStartTime();

        if (null != cronModel.getSecond()
                && null != cronModel.getMinute()
                && null != cronModel.getHour()) {
            //按每天
            if (cronModel.getJobType().getValue() == 1) {
                Integer beApart = cronModel.getBeApart();
                if (beApart != null) {
                    description.append("每间隔").append(beApart).append("天");
                } else {
                    description.append("每天");
                }
                description.append(cronModel.getHour()).append("时");
                description.append(cronModel.getMinute()).append("分");
                description.append(cronModel.getSecond()).append("秒");
                description.append("执行");
            }

            //按每周
            else if (cronModel.getJobType().getValue() == 3) {
                if (cronModel.getDayOfWeeks() != null && cronModel.getDayOfWeeks().length > 0) {
                    String days = "";
                    for (int i : cronModel.getDayOfWeeks()) {
                        days += "周" + i;
                    }
                    description.append("每周的").append(days).append(" ");
                }
                if (null != cronModel.getSecond()
                        && null != cronModel.getMinute()
                        && null != cronModel.getHour()) {
                    description.append(",");
                    description.append(cronModel.getHour()).append("时");
                    description.append(cronModel.getMinute()).append("分");
                    description.append(cronModel.getSecond()).append("秒");
                }
                description.append("执行");
            }

            //按每月
            else if (cronModel.getJobType().getValue() == 2) {
                //选择月份
                if (cronModel.getDayOfMonths() != null && cronModel.getDayOfMonths().length > 0) {
                    String days = "";
                    for (int i : cronModel.getDayOfMonths()) {
                        days += i + "号";
                    }
                    description.append("每月的").append(days).append(" ");
                }
                description.append(cronModel.getHour()).append("时");
                description.append(cronModel.getMinute()).append("分");
                description.append(cronModel.getSecond()).append("秒");
                description.append("执行");
            }

        }
        return description.toString();
    }


    /**
     * 构建Cron表达式
     *
     * @param rate  第几位
     * @param cycle 数值
     * @return
     */
    public static String createLoopCronExpression(int rate, int cycle) {
        String cron = "";
        switch (rate) {
            case 0:// 每cycle秒执行一次
                cron = "0/" + cycle + " * * * * ?";
                break;
            case 1:// 每cycle分钟执行一次
                cron = "0 0/" + cycle + " * * * ?";
                break;
            case 2:// 每cycle小时执行一次
                cron = "0 0 0/" + cycle + " * * ?";
                break;
            case 3:// 每cycle天的0点执行一次
                cron = "0 0 0 1/" + cycle + " * ?";
                break;
            case 4:// 每cycle月的1号0点执行一次
                cron = "0 0 0 1 1/" + cycle + " ? ";
                break;
            case 5://  每天cycle点执行一次
                cron = "0 0 " + cycle + "  * * ?";
                break;
            default:// 默认每cycle秒执行一次
                cron = "0/1 * * * * ?";
                break;
        }
        return cron;
    }


}