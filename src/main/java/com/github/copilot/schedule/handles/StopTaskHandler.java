package com.github.copilot.schedule.handles;

import com.github.copilot.schedule.scheduler.TaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("STOP_TASK")
public class StopTaskHandler implements NotifyHandler<Long> {

    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public void update(Long taskId) {
        taskExecutor.stop(taskId);
    }

}
