package com.github.copilot.schedule.utils;

import com.github.copilot.schedule.enums.JobEnum;
import com.github.copilot.schedule.model.CronModel;
import com.github.copilot.util.ArrayUtil;
import com.github.copilot.util.DateUtil;
import com.github.copilot.util.StringUtil;
import org.quartz.CronTrigger;
import org.quartz.TriggerUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 字段 允许值 允许的特殊字符
 * 秒 0-59 , - * /
 * 分 0-59 , - * /
 * 小时 0-23 , - * /
 * 日期 1-31 , - * ? / L W C
 * 月份 1-12 或者 JAN-DEC , - * /
 * 星期 1-7 或者 SUN-SAT , - * ? / L C #
 * 年（可选） 留空, 1970-2099 , - * /
 * <p>
 * * 表示所有值；
 * ? 表示未说明的值，即不关心它为何值；
 * - 表示一个指定的范围；
 * , 表示附加一个可能值；
 * / 符号前表示开始时间，符号后表示每次递增的值；
 * L("last") ("last") "L" 用在day-of-month字段意思是 "这个月最后一天"；用在 day-of-week字段, 它简单意思是 "7" or "SAT"。 如果在day-of-week字段里和数字联合使用，它的意思就是 "这个月的最后一个星期几" – 例如： "6L" means "这个月的最后一个星期五". 当我们用“L”时，不指明一个列表值或者范围是很重要的，不然的话，我们会得到一些意想不到的结果。
 * W("weekday") 只能用在day-of-month字段。用来描叙最接近指定天的工作日（周一到周五）。例如：在day-of-month字段用“15W”指“最接近这个 月第15天的工作日”，即如果这个月第15天是周六，那么触发器将会在这个月第14天即周五触发；如果这个月第15天是周日，那么触发器将会在这个月第 16天即周一触发；如果这个月第15天是周二，那么就在触发器这天触发。注意一点：这个用法只会在当前月计算值，不会越过当前月。“W”字符仅能在 day-of-month指明一天，不能是一个范围或列表。也可以用“LW”来指定这个月的最后一个工作日。
 * # 只能用在day-of-week字段。用来指定这个月的第几个周几。例：在day-of-week字段用"6#3"指这个月第3个周五（6指周五，3指第3个）。如果指定的日期不存在，触发器就不会触发。
 * C 指和calendar联系后计算过的值。例：在day-of-month 字段用“5C”指在这个月第5天或之后包括calendar的第一天；在day-of-week字段用“1C”指在这周日或之后包括calendar的第一天。
 */
public class CronUtil {

    /**
     * 计算表达式最近几次执行时间
     *
     * @param cronExpress 表达式
     * @param num         次数
     * @return 时间集合
     */
    public static List<String> getCronNextTimes(String cronExpress, Integer num) {
        if (StringUtil.isEmpty(cronExpress)) {
            throw new RuntimeException("cron 表达式不能为空");
        }
        //判断cron表达式
        if (!CronSequenceGenerator.isValidExpression(cronExpress)) {
            throw new RuntimeException("cron 表达式格式不正确,cron: " + cronExpress);
        }
        if (num == null || num == 0) {
            num = 1;
        }
        List<String> list = new ArrayList<>();
        CronTrigger cronTrigger = new CronTrigger();
        try {
            cronTrigger.setCronExpression(cronExpress);
        } catch (ParseException e) {
            throw new RuntimeException("cron表达式不正确,cron: " + cronExpress);
        }
        List<Date> dates = TriggerUtils.computeFireTimes(cronTrigger, null, num);
        String format = "yyyy-MM-dd HH:mm:ss";
        for (Date date : dates) {
            list.add(DateUtil.format(date, format));
        }
        return list;
    }

    /**
     * 构建Cron表达式
     * 目前支持三种常用的cron表达式
     * 1.每天的某个时间点执行 例:12 12 12 * * 表示每天12时12分12秒执行
     * 2.每周的哪几天执行    例:12 12 12 ? * 1,2,3表示每周的周1周2周3 ,12时12分12秒执行
     * 3.每月的哪几天执行    例:12 12 12 1,21,13 * ?表示每月的1号21号13号 12时12分12秒执行
     */
    public static String createCronExpression(CronModel cronModel) {
        StringBuilder cronExp = new StringBuilder();

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

    public static void main(String[] args) {
        CronModel cronModel = new CronModel();
        cronModel.setJobType(JobEnum.DAY);//按每天
        //每隔几天执行
        cronModel.setBeApart(1);
        String cropExp = createCronExpression(cronModel);
        System.out.println(cropExp + ":" + createDescription(cronModel));
        //执行时间：每天的12时12分12秒 end
        System.out.println(getCronNextTimes(cropExp, 5));

        cronModel.setJobType(JobEnum.WEEK);//每周的哪几天执行
        Integer[] dayOfWeeks = new Integer[3];
        dayOfWeeks[0] = 1;
        dayOfWeeks[1] = 2;
        dayOfWeeks[2] = 3;
        cronModel.setDayOfWeeks(dayOfWeeks);
        cronModel.setJobType(JobEnum.WEEK);
        cropExp = createCronExpression(cronModel);
        System.out.println(cropExp + ":" + createDescription(cronModel));
        System.out.println(getCronNextTimes(cropExp, 5));

        cronModel.setJobType(JobEnum.MONTH);//每月的哪几天执行
        Integer[] dayOfMonths = new Integer[3];
        dayOfMonths[0] = 1;
        dayOfMonths[1] = 21;
        dayOfMonths[2] = 13;
        cronModel.setDayOfMonths(dayOfMonths);
        cropExp = createCronExpression(cronModel);
        System.out.println(cropExp + ":" + createDescription(cronModel));
        System.out.println(getCronNextTimes(cropExp, 5));

        cronModel.setJobType(JobEnum.EVERY);//每天的几点几分几秒开始
        cropExp = createCronExpression(cronModel);
        System.out.println(cropExp);
        System.out.println(getCronNextTimes(cropExp, 5));

    }
}