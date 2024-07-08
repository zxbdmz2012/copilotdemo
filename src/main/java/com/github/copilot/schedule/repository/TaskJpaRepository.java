package com.github.copilot.schedule.repository;

import com.github.copilot.schedule.entity.Task;
import com.github.copilot.schedule.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface TaskJpaRepository extends JpaRepository<Task, Long> {
    Task findByName(String name);

    List<Task> findByStatusAndnextStartTimeBefore(TaskStatus taskStatus, LocalDateTime localDateTime);

    int updateStatusByNodeId(TaskStatus taskStatus, String nodeId);

    List<Task> findByStatusInAndUpdateTimeBefore(ArrayList<TaskStatus> taskStatuses, LocalDateTime localDateTime);
}
