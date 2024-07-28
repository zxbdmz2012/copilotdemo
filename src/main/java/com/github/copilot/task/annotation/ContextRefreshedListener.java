package com.github.copilot.task.annotation;

import com.github.copilot.task.common.Invocation;
import com.github.copilot.task.config.EasyJobConfig;
import com.github.copilot.task.entity.Task;
import com.github.copilot.task.enums.JobEnum;
import com.github.copilot.task.model.CronModel;
import com.github.copilot.task.repository.TaskRepository;
import com.github.copilot.task.scheduler.ScheduleTaskExecutor;
import com.github.copilot.task.utils.CronUtil;
import com.github.copilot.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
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

@Component
@Slf4j
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    public final static String DOT = ".";

    @Autowired
    private ScheduleTaskExecutor scheduleTaskExecutor;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EasyJobConfig config;

    // Store all task names in the database
    private List<String> allTaskNames;

    private Map<String, Task> taskMap = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Check if recovery and heartbeat functions are enabled
        if (config.isRecoverEnable() && config.isHeartBeatEnable()) {

            // Initialize system start time to solve the problem of executing tasks according to the previous time after system restart
            config.setSysStartTime(new Date());

            // Reinitialize the task status of this node after restart
            taskRepository.reInitTasks();

            // Retrieve all task names from the database
            allTaskNames = taskRepository.listAllTaskNames();

            // Retrieve all tasks from the database
            taskMap = taskRepository.listAllTasks();

            // Check if the root container is the Spring container to prevent calling twice (mvc loading will also trigger once)
            if (event.getApplicationContext().getParent() == null) {

                // Check if the scheduling switch is turned on
                // If it is turned on: load the scheduling annotation and add the schedule to the schedule management
                ApplicationContext context = event.getApplicationContext();
                Map<String, Object> beans = context.getBeansWithAnnotation(org.springframework.scheduling.annotation.EnableScheduling.class);
                if (beans == null) {
                    return;
                }

                // Used to store the mapping between method names and Method objects that are decorated with scheduling annotations
                Map<String, Method> methodMap = new HashMap<>();

                // Find all classes directly or indirectly decorated with the Component annotation, because Service, Controller, etc. all contain Component, that is,
                // As long as it is a class managed by the spring container, it must be directly or indirectly decorated with Component
                Map<String, Object> allBeans = context.getBeansWithAnnotation(org.springframework.stereotype.Component.class);
                Set<Map.Entry<String, Object>> entrys = allBeans.entrySet();

                // Traverse the beans and their methods to find methods decorated with the Scheduled annotation, and then add the tasks to the task scheduler
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

                // Since taskIdMap is only used once after spring starts, it can be cleared directly here
                allTaskNames.clear();
                taskMap.clear();
            }
        }
    }

    // Recursively add parent and child tasks
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
        String finalName = clazz.getName() + DOT + name;

        String cronExpression;

        // If rate is not empty, use rate and cycle to generate cron expression
        if (StringUtil.isNotEmpty(rate)) {
            final int rateInt = Integer.parseInt(rate);
            final int cycleInt = Integer.parseInt(cycle);
            final CronModel cronModel = new CronModel();
            cronModel.setRateInt(rateInt);
            cronModel.setCycleInt(cycleInt);
            cronExpression = CronUtil.createCronExpression(cronModel);
        } else {
            // Otherwise, use hour, min, sec, and timezone to generate cron expression
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

        // If the task name is not in the list of all task names, add the task
        if (!allTaskNames.contains(finalName)) {
            Long taskId = null;
            try {
                taskId = scheduleTaskExecutor.addTask(finalName, cronExpression, new Invocation(clazz, name, m.getParameterTypes()));
                log.info("add taskId {} taskName{} success", taskId, finalName);
            } catch (Exception e) {
                log.error("add taskId {} taskName{} fail", taskId, finalName);
            }
        } else {
            // Otherwise, update the task
            final Task task = taskMap.get(finalName);
            if (!task.getCronExpr().equalsIgnoreCase(cronExpression)) {
                task.setCronExpr(cronExpression);
                taskRepository.updateTask(task);
                log.info("update taskId {} taskName{} success", task.getId(), finalName);
            } else {
                log.info("taskId {} taskName{} cronExpr is same, no need to update", task.getId(), finalName);
            }
        }
    }
}