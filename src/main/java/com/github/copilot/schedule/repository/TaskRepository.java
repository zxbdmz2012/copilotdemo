package com.github.copilot.schedule.repository;

import com.github.copilot.schedule.common.Invocation;
import com.github.copilot.schedule.config.EasyJobConfig;
import com.github.copilot.schedule.entity.Task;
import com.github.copilot.schedule.entity.TaskDetail;
import com.github.copilot.schedule.enums.TaskStatus;
import com.github.copilot.schedule.serializer.JdkSerializationSerializer;
import com.github.copilot.schedule.serializer.ObjectSerializer;
import com.github.copilot.schedule.utils.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务对象数据库操作对象
 */
@Component
public class TaskRepository {
    private static final Logger log = LoggerFactory.getLogger(TaskRepository.class);
    /**
     * 序列化工具类
     */
    private final ObjectSerializer serializer = new JdkSerializationSerializer<Invocation>();
    @Resource
    private NodeJpaRepository nodeJpaRepository;
    @Resource
    private TaskJpaRepository taskJpaRepository;
    @Resource
    private TaskDetailJpaRepository taskDetailJpaRepository;
    @Autowired
    private EasyJobConfig config;

    /**
     * 查询还需要指定时间才开始的主任务列表
     * TIMESTAMPDIFF(SECOND,NOW(),next_start_time) < ? 这里用不到索引，会有效率问题
     * next_start_time < date_sub(now(), interval ? second) 改成这种方式就好了
     *
     * @param duration
     * @return
     */
    public List<Task> listNotStartedTasks(int duration) {
        return taskJpaRepository.findByStatusAndnextStartTimeBefore(TaskStatus.NOT_STARTED, LocalDateTime.now().minusSeconds(duration));
    }

    /**
     * 查找所有的任务名称
     *
     * @return
     */
    public List<String> listAllTaskNames() {
        return taskJpaRepository.findAll().stream().map(Task::getName).collect(Collectors.toList());
    }

    /**
     * 列出指定任务的任务详情
     *
     * @param taskId 任务id
     * @return
     */
    public List<TaskDetail> listDetails(Long taskId) {
        return taskDetailJpaRepository.findByTaskId(taskId);
    }


    /**
     * 列出需要恢复的任务，需要恢复的任务是指所属执行节点已经挂了并且该任务还属于执行中的任务
     * timestampdiff(SECOND,n.update_time,now()) > ? 这种用不到索引
     * n.update_time < date_sub(now(), interval ? second) 换成这种
     *
     * @param timeout 超时时间
     * @return
     */
    public List<Task> listRecoverTasks(int timeout) {

        ArrayList<TaskStatus> taskStatuses = new ArrayList<>();
        taskStatuses.add(TaskStatus.DOING);
        taskStatuses.add(TaskStatus.ERROR);

        return taskJpaRepository.findByStatusInAndUpdateTimeBefore(taskStatuses, LocalDateTime.now().minusSeconds(timeout));
    }

    /**
     * 根据指定id获取具体任务对象
     *
     * @param id
     * @return
     */
    public Task get(Long id) {
        Task task = taskJpaRepository.findById(id).orElse(null);
        if (task != null) {
            task.setInvokor((Invocation) serializer.deserialize(task.getInvokeInfo()));
        }
        return task;
    }

    /**
     * 根据指定id获取具体任务明细对象
     *
     * @param id
     * @return
     */
    public TaskDetail getDetail(Long id) {
        return taskDetailJpaRepository.findById(id).orElse(null);
    }


    /**
     * 插入任务
     *
     * @param task 待插入任务
     * @return
     * @throws Exception
     */
    public long insert(Task task) throws Exception {
        CronExpression cronExpession = new CronExpression(task.getCronExpr());
        Date nextStartDate = cronExpession.getNextValidTimeAfter(new Date());
        task.setFirstStartTime(nextStartDate);
        task.setNextStartTime(nextStartDate);
        task.setStatus(TaskStatus.NOT_STARTED);
        Task save = taskJpaRepository.save(task);
        return save.getId();
    }

    /**
     * 插入任务详情
     *
     * @param taskDetail 待插入任务详情
     * @return
     */
    public long insert(TaskDetail taskDetail) {
        TaskDetail save = taskDetailJpaRepository.save(taskDetail);
        return save.getId();
    }

    /**
     * 开始一个任务
     *
     * @param task 待开始的任务
     * @return
     * @throws Exception
     */
    public TaskDetail start(Task task) throws Exception {
        TaskDetail taskDetail = new TaskDetail(task.getId());
        taskDetail.setNodeId(task.getNodeId());
        long id = insert(taskDetail);
        taskDetail.setId(id);
        return taskDetail;
    }


    /**
     * 完成任务
     *
     * @param task   待开始的任务
     * @param detail 本次执行的具体任务详情
     * @throws Exception
     */
    public void finish(Task task, TaskDetail detail) throws Exception {
        CronExpression cronExpession = new CronExpression(task.getCronExpr());
        Date nextStartDate = cronExpession.getNextValidTimeAfter(task.getNextStartTime());
        /**
         *  如果没有下次执行时间了，该任务就完成了，反之变成未开始
         */
        if (nextStartDate == null) {
            task.setStatus(TaskStatus.FINISH);
        } else {
            task.setStatus(TaskStatus.NOT_STARTED);
        }
        /**
         * 增加任务成功次数
         */
        task.setSuccessCount(task.getSuccessCount() + 1);
        task.setNextStartTime(nextStartDate);
        /**
         * 使用乐观锁检测是否可以更新成功，成功则更新详情
         */
        if (updateTask(task)) {
            detail.setEndTime(new Date());
            detail.setStatus(TaskStatus.FINISH);
            updateTaskDetail(detail);
        }
    }

    /**
     * 记录任务失败信息
     *
     * @param task     待失败任务
     * @param detail   任务详情
     * @param errorMsg 出错信息
     * @throws Exception
     */
    public void fail(Task task, TaskDetail detail, String errorMsg) throws Exception {
        if (detail == null) return;
        //如果没有下次执行时间了，该任务就完成了，反之变成待执行
        task.setStatus(TaskStatus.ERROR);
        task.setFailCount(task.getFailCount() + 1);
        if (updateTask(task)) {
            detail.setEndTime(new Date());
            detail.setStatus(TaskStatus.ERROR);
            detail.setErrorMsg(errorMsg);
            updateTaskDetail(detail);
        }
    }

    /**
     * 重启服务后，重新把本节点的任务初始化为初始状态
     *
     * @return
     * @throws Exception
     */
    public int reInitTasks() {

        return taskJpaRepository.updateStatusByNodeId(TaskStatus.NOT_STARTED, config.getNodeId());
    }


    public int reInitTasks(String nodeId) {
        return taskJpaRepository.updateStatusByNodeId(TaskStatus.NOT_STARTED, nodeId);
    }


    public boolean updateTaskDetail(TaskDetail taskDetail) {
        int maxRetries = 3; // Maximum number of retries
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                taskDetailJpaRepository.save(taskDetail);
                return true; // Optimistic lock success, entity saved successfully
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                log.error("Attempt {} to update taskDetail {} failed", attempt + 1, taskDetail.getId(), e);
                // Last attempt, no more retries left
                if (attempt == maxRetries - 1) {
                    log.error("Final attempt to update taskDetail {} failed", taskDetail.getId(), e);
                    return false; // Optimistic lock failed, entity save failed after all retries
                }
            }
        }
        return false; // Should never reach here
    }


    public boolean updateTask(Task task) {
        int retryCount = 3; // Number of retries
        while (retryCount >= 0) {
            try {
                taskJpaRepository.save(task);
                return true; // Optimistic lock success, entity saved successfully
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                log.error("update task {} error, retrying...", task.getId(), e);
                retryCount--;
                if (retryCount < 0) {
                    log.error("update task {} error after retry", task.getId(), e);
                    return false; // Optimistic lock failed, entity save failed
                }
            }
        }
        return false;
    }

    public Task getTaskById(Long taskId) {
        return taskJpaRepository.findById(taskId).orElse(null);
    }
}
