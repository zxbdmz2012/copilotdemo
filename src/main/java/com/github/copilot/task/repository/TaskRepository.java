package com.github.copilot.task.repository;

import com.github.copilot.task.common.Invocation;
import com.github.copilot.task.config.EasyJobConfig;
import com.github.copilot.task.entity.Task;
import com.github.copilot.task.entity.TaskDetail;
import com.github.copilot.task.enums.TaskStatus;
import com.github.copilot.task.serializer.JsonSerializationSerializer;
import com.github.copilot.task.serializer.ObjectSerializer;
import com.github.copilot.task.utils.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository class for managing task entities and their lifecycle.
 * This class provides methods for querying, inserting, and updating tasks and task details in the database.
 * It includes functionality for listing tasks based on their start times, managing task execution details,
 * and handling task status updates with optimistic locking to ensure data consistency.
 */
@Component
public class TaskRepository {
    private static final Logger log = LoggerFactory.getLogger(TaskRepository.class);

    /**
     * Serializer for task invocation information.
     */
    private final ObjectSerializer serializer = new JsonSerializationSerializer<Invocation>();

    @Resource
    private NodeJpaRepository nodeJpaRepository;

    @Resource
    private TaskJpaRepository taskJpaRepository;

    @Resource
    private TaskDetailJpaRepository taskDetailJpaRepository;

    @Autowired
    private EasyJobConfig config;

    public Map<String,Task> listAllTasks(){
        List<Task> all = taskJpaRepository.findAll();
        Map<String, Task> map = new HashMap<>();
        for (Task task : all) {
            map.put(task.getName(), task);
        }
        return map;
    }

    public void del(Long taskId){
        taskJpaRepository.deleteById(taskId);
    }
    /**
     * Lists tasks that are not started and are scheduled to start within a specified duration.
     *
     * @param duration The duration in seconds before the scheduled start time of the tasks.
     * @return A list of tasks that meet the criteria.
     */
    public List<Task> listNotStartedTasks(int duration) {
        Date currentDateTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDateTime);
        calendar.add(Calendar.SECOND, -duration);
        return taskJpaRepository.findByStatusAndNextStartTimeBefore(TaskStatus.NOT_STARTED, calendar.getTime());
    }

    /**
     * Lists all task names present in the database.
     *
     * @return A list of all task names.
     */
    public List<String> listAllTaskNames() {
        return taskJpaRepository.findAll().stream().map(Task::getName).collect(Collectors.toList());
    }

    /**
     * Lists details for a specific task identified by its ID.
     *
     * @param taskId The ID of the task.
     * @return A list of task details associated with the specified task.
     */
    public List<TaskDetail> listDetails(Long taskId) {
        return taskDetailJpaRepository.findByTaskId(taskId);
    }

    /**
     * Lists tasks that need to be recovered. A task needs recovery if its executing node is down and the task is still marked as executing.
     *
     * @param timeout The timeout in seconds to consider a node as down.
     * @return A list of tasks that meet the recovery criteria.
     */
    public List<Task> listRecoverTasks(int timeout) {
        ArrayList<TaskStatus> taskStatuses = new ArrayList<>();
        taskStatuses.add(TaskStatus.DOING);
        taskStatuses.add(TaskStatus.ERROR);

        return taskJpaRepository.findByStatusInAndUpdateTimeBefore(taskStatuses, LocalDateTime.now().minusSeconds(timeout));
    }

    /**
     * Retrieves a specific task by its ID.
     *
     * @param id The ID of the task.
     * @return The task if found, null otherwise.
     */
    public Task get(Long id) {
        Task task = taskJpaRepository.findById(id).orElse(null);
        if (task != null) {
            task.setInvokor((Invocation) serializer.deserialize(task.getInvokeInfo()));
        }
        return task;
    }

    /**
     * Retrieves a specific task detail by its ID.
     *
     * @param id The ID of the task detail.
     * @return The task detail if found, null otherwise.
     */
    public TaskDetail getDetail(Long id) {
        return taskDetailJpaRepository.findById(id).orElse(null);
    }

    /**
     * Inserts a new task into the database.
     *
     * @param task The task to insert.
     * @return The ID of the inserted task.
     * @throws Exception If there is an error during insertion.
     */
    public long insert(Task task) throws Exception {
        CronExpression cronExpression = new CronExpression(task.getCronExpr());
        Date nextStartDate = cronExpression.getNextValidTimeAfter(new Date());
        task.setFirstStartTime(nextStartDate);
        task.setNextStartTime(nextStartDate);
        task.setStatus(TaskStatus.NOT_STARTED);
        Task save = taskJpaRepository.save(task);
        return save.getId();
    }

    /**
     * Inserts a new task detail into the database.
     *
     * @param taskDetail The task detail to insert.
     * @return The ID of the inserted task detail.
     */
    public long insert(TaskDetail taskDetail) {
        TaskDetail save = taskDetailJpaRepository.save(taskDetail);
        return save.getId();
    }

    /**
     * Starts a task by creating a new task detail record for it.
     *
     * @param task The task to start.
     * @return The newly created task detail.
     * @throws Exception If there is an error during the start operation.
     */
    public TaskDetail start(Task task) throws Exception {
        TaskDetail taskDetail = new TaskDetail(task.getId());
        taskDetail.setNodeId(task.getNodeId());
        long id = insert(taskDetail);
        taskDetail.setId(id);
        return taskDetail;
    }

    /**
     * Marks a task and its detail as finished and calculates the next start time based on the cron expression.
     *
     * @param task The task to mark as finished.
     * @param detail The task detail associated with the current execution.
     * @throws Exception If there is an error during the finish operation.
     */
    public void finish(Task task, TaskDetail detail) throws Exception {
        CronExpression cronExpression = new CronExpression(task.getCronExpr());
        Date nextStartDate = cronExpression.getNextValidTimeAfter(task.getNextStartTime());
        if (nextStartDate == null) {
            task.setStatus(TaskStatus.FINISH);
        } else {
            task.setStatus(TaskStatus.NOT_STARTED);
        }
        task.setSuccessCount(task.getSuccessCount() + 1);
        task.setNextStartTime(nextStartDate);
        if (updateTask(task)) {
            detail.setEndTime(new Date());
            detail.setStatus(TaskStatus.FINISH);
            updateTaskDetail(detail);
        }
    }

    /**
     * Records failure information for a task and its detail.
     *
     * @param task The task that failed.
     * @param detail The task detail associated with the failed execution.
     * @param errorMsg The error message describing the failure.
     * @throws Exception If there is an error during the failure recording operation.
     */
    public void fail(Task task, TaskDetail detail, String errorMsg) throws Exception {
        if (detail == null) return;
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
     * Reinitializes tasks to the NOT_STARTED status for the current node after a service restart.
     *
     * @return The number of tasks reinitialized.
     * @throws Exception If there is an error during the reinitialization.
     */
    public int reInitTasks() {
        return taskJpaRepository.updateTaskStatusByNodeId(TaskStatus.NOT_STARTED, config.getNodeId());
    }

    /**
     * Reinitializes tasks to the NOT_STARTED status for a specified node.
     *
     * @param nodeId The ID of the node for which tasks should be reinitialized.
     * @return The number of tasks reinitialized.
     */
    public int reInitTasks(String nodeId) {
        return taskJpaRepository.updateTaskStatusByNodeId(TaskStatus.NOT_STARTED, nodeId);
    }

    /**
     * Attempts to update a task detail in the database, retrying up to a maximum number of times in case of optimistic locking failures.
     *
     * @param taskDetail The task detail to update.
     * @return true if the update was successful, false otherwise.
     */
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

    /**
     * Attempts to update a task in the database, retrying in case of optimistic locking failures.
     *
     * @param task The task to update.
     * @return true if the update was successful, false otherwise.
     */
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

    /**
     * Retrieves a task by its ID.
     *
     * @param taskId The ID of the task to retrieve.
     * @return The task if found, null otherwise.
     */
    public Task getTaskById(Long taskId) {
        return taskJpaRepository.findById(taskId).orElse(null);
    }
}