package com.github.copilot.schedule.handles;

import com.github.copilot.schedule.scheduler.TaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handler for stopping tasks.
 * This component listens for stop task notifications and invokes the task executor to stop the specified task.
 */
@Component("STOP_TASK")
public class StopTaskHandler implements NotifyHandler<Long> {

    @Autowired
    private TaskExecutor taskExecutor;

    /**
     * Stops the task with the given task ID.
     *
     * @param taskId The ID of the task to be stopped.
     */
    @Override
    public void update(Long taskId) {
        taskExecutor.stop(taskId);
    }
}