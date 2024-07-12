package com.github.copilot.task.repository;

import com.github.copilot.task.entity.TaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDetailJpaRepository extends JpaRepository<TaskDetail, Long> {
    List<TaskDetail> findByTaskId(Long taskId);

}
