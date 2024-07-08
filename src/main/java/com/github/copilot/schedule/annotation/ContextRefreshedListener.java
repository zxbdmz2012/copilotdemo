package com.github.copilot.schedule.annotation;


import com.github.copilot.schedule.common.Invocation;
import com.github.copilot.schedule.config.EasyJobConfig;
import com.github.copilot.schedule.entity.Task;
import com.github.copilot.schedule.enums.JobEnum;
import com.github.copilot.schedule.model.CronModel;
import com.github.copilot.schedule.repository.TaskRepository;
import com.github.copilot.schedule.scheduler.TaskExecutor;
import com.github.copilot.schedule.utils.CronUtil;
import com.github.copilot.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.*;
import java.util.*;

import static com.github.copilot.util.DateUtil.getzonedDateTime;


/**
 * Listener for handling context refreshed events in a Spring application.
 * This class listens for {@link ContextRefreshedEvent} to perform initialization tasks
 * once the Spring application context is fully loaded and refreshed. It is particularly
 * useful for tasks that need to be executed right after the application startup, such as
 * initializing or reinitializing job tasks, loading necessary configurations, or performing
 * any startup logic that requires access to the fully initialized Spring context.
 * <p>
 * The {@link #onApplicationEvent(ContextRefreshedEvent)} method is triggered when the
 * application context is refreshed. This method performs several key operations:
 * - Sets the system startup time to handle task execution timing after restarts.
 * - Reinitializes tasks to reset their status upon application restart.
 * - Fetches all task names from the database to manage tasks effectively.
 * - Ensures that it only operates on the root application context to avoid being executed
 * multiple times in a hierarchical context environment (e.g., when using Spring MVC).
 * - Optionally, if scheduling is enabled, it loads and processes tasks annotated with
 * {@link Scheduled} to add them to the task scheduler.
 * <p>
 * It leverages the {@link TaskExecutor} to manage task scheduling and execution, and
 * {@link TaskRepository} to interact with the database for task management. The
 * {@link EasyJobConfig} is used to store configuration settings relevant to job execution.
 * <p>
 * This class demonstrates a practical use of application listeners in Spring for
 * application lifecycle management and dynamic task scheduling based on annotations.
 */

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    public final static String DOT = ".";
    private static final Logger log = LoggerFactory.getLogger(ContextRefreshedListener.class);

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EasyJobConfig config;


    /**
     * 用来保存方法名/任务名和任务插入后数据库的ID的映射,用来处理子任务新增用
     */
    private final Map<String, Long> taskIdMap = new HashMap<>();

    /**
     * 存放数据库所有的任务名称
     */
    private List<String> allTaskNames;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        /**
         * 初始化系统启动时间,用于解决系统重启后，还是按照之前时间执行任务
         */
        config.setSysStartTime(new Date());
        /**
         * 重启重新初始化本节点的任务状态
         */
        taskRepository.reInitTasks();
        /**
         * 查出数据库所有的任务名称
         */
        allTaskNames = taskRepository.listAllTaskNames();
        /**
         * 判断根容器为Spring容器，防止出现调用两次的情况（mvc加载也会触发一次）
         */
        if (event.getApplicationContext().getParent() == null) {
            /**
             * 判断调度开关是否打开
             * 如果打开了：加载调度注解并将调度添加到调度管理中
             */
            ApplicationContext context = event.getApplicationContext();
            Map<String, Object> beans = context.getBeansWithAnnotation(org.springframework.scheduling.annotation.EnableScheduling.class);
            if (beans == null) {
                return;
            }
            /**
             * 用来存放被调度注解修饰的方法名和Method的映射
             */
            Map<String, Method> methodMap = new HashMap<>();
            /**
             * 查找所有直接或者间接被Component注解修饰的类，因为不管Service，Controller等都包含了Component，也就是
             * 只要是被纳入了spring容器管理的类必然直接或者间接的被Component修饰
             */
            Map<String, Object> allBeans = context.getBeansWithAnnotation(org.springframework.stereotype.Component.class);
            Set<Map.Entry<String, Object>> entrys = allBeans.entrySet();
            /**
             * 遍历bean和里面的method找到被Scheduled注解修饰的方法,然后将任务放入任务调度里
             */
            for (Map.Entry entry : entrys) {
                Object obj = entry.getValue();
                Class clazz = obj.getClass();
                Method[] methods = clazz.getMethods();
                for (Method m : methods) {
                    if (m.isAnnotationPresent(Scheduled.class)) {
                        handleSheduledAnn(m);
                    }
                }
            }

            /**
             * 由于taskIdMap只是启动spring完成后使用一次，这里可以直接清空
             */
            taskIdMap.clear();
        }
    }

    /**
     * 递归添加父子任务
     *
     * @param m
     * @throws Exception
     */
    private void handleSheduledAnn(Method m) {
        Class<?> clazz = m.getDeclaringClass();
        String name = m.getName();
        Scheduled sAnn = m.getAnnotation(Scheduled.class);
        String hour = sAnn.hour();
        String min = sAnn.min();
        String sec = sAnn.sec();
        String timezone = sAnn.timezone();
        String rate = sAnn.rate();
        String cycle = sAnn.cycle();
        String cron = sAnn.cron();
        String finalName = clazz.getName() + DOT + name;

        String cronExpression;

        if (StringUtil.isNotEmpty(rate)) {

            final int rateInt = Integer.parseInt(rate);

            final int cycleInt = Integer.parseInt(cycle);

            final CronModel cronModel = new CronModel();

            cronModel.setRateInt(rateInt);

            cronModel.setCycleInt(cycleInt);

            cronExpression = CronUtil.createCronExpression(cronModel);

        } else {

            LocalDate startLocalDate = LocalDate.now();

            LocalTime localTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(min), Integer.parseInt(sec));

            LocalDateTime startLocalDateTime = startLocalDate.atTime(localTime);

            ZonedDateTime startTime = getzonedDateTime(startLocalDateTime, timezone);

            startTime = startTime.withZoneSameInstant(ZoneId.systemDefault());

            Date startTimeDate = Date.from(startTime.toInstant());

            final String jobtype = sAnn.jobType();

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(startTimeDate);

            CronModel cronModel = new CronModel();

            cronModel.setJobType(JobEnum.valueOf(jobtype));

            cronModel.setHour(calendar.get(Calendar.HOUR_OF_DAY));

            cronModel.setMinute(calendar.get(Calendar.MINUTE));

            cronModel.setSecond(calendar.get(Calendar.SECOND));

            cronExpression = CronUtil.createCronExpression(cronModel);
        }


        if (!allTaskNames.contains(finalName)) {
            Long taskId = null;
            try {
                taskId = taskExecutor.addTask(finalName, cronExpression, new Invocation(clazz, name, m.getParameterTypes()));
                log.info("add taskId {} taskName{} success", taskId, finalName);
            } catch (Exception e) {
                log.error("add taskId {} taskName{} fail", taskId, finalName);
            }
        } else {
            Long taskId = taskIdMap.get(finalName);
            final Task task = taskRepository.getTaskById(taskId);
            if (!task.getCronExpr().equalsIgnoreCase(cronExpression)) {
                task.setCronExpr(cronExpression);
                taskRepository.updateTask(task);
                log.info("update taskId {} taskName{} success", taskId, finalName);
            } else {
                log.info("taskId {} taskName{} cronExpr is same, no need to update", taskId, finalName);
            }

        }

    }
}
