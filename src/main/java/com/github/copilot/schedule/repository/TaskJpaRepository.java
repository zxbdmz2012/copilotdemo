package com.github.copilot.schedule.repository;

import com.github.copilot.schedule.entity.Task;
import com.github.copilot.schedule.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA repository interface for {@link Task} entities.
 * Defines methods for querying and updating tasks based on their status, name, and scheduled execution times.
 * It supports operations such as finding tasks by name, status, and scheduled start times, updating task status by node ID,
 * and finding tasks by multiple statuses and their last update time.
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<Task, Long> {
    /**
     * Finds a task by its name.
     *
     * @param name The name of the task.
     * @return The task entity, or null if not found.
     */
    Task findByName(String name);

    /**
     * Finds tasks that are in a specific status and scheduled to start before a given time.
     *
     * @param taskStatus The status of the tasks to find.
     * @param localDateTime The time before which the tasks are scheduled to start.
     * @return A list of tasks matching the criteria.
     */
    List<Task> findByStatusAndnextStartTimeBefore(TaskStatus taskStatus, LocalDateTime localDateTime);

    /**
     * Updates the status of tasks assigned to a specific node.
     *
     * @param taskStatus The new status to set for the tasks.
     * @param nodeId The ID of the node whose tasks are to be updated.
     * @return The number of tasks updated.
     */
    int updateStatusByNodeId(TaskStatus taskStatus, String nodeId);

    /**
     * Finds tasks that are in any of the specified statuses and were last updated before a given time.
     *
     * @param taskStatuses The statuses of the tasks to find.
     * @param localDateTime The time before which the tasks were last updated.
     * @return A list of tasks matching the criteria.
     */
    List<Task> findByStatusInAndUpdateTimeBefore(ArrayList<TaskStatus> taskStatuses, LocalDateTime localDateTime);
}