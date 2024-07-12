package com.github.copilot.task.enums;

import lombok.Getter;

/**
 * Enum representing the various statuses a task can have within the system.
 * It defines the lifecycle stages of a task from not started to completed or stopped.
 */
@Getter
public enum TaskStatus {

    // Indicates the task has not started yet.
    NOT_STARTED(0),
    // Indicates the task is scheduled and waiting to be executed.
    PENDING(1),
    // Indicates the task is currently being executed.
    DOING(2),
    // Indicates the task encountered an error during execution.
    ERROR(3),
    // Indicates the task has completed its execution successfully.
    FINISH(4),
    // Indicates the task has been manually stopped before completion.
    STOP(5);

    int id;

    TaskStatus(int id) {
        this.id = id;
    }

    public static TaskStatus valueOf(int id) {
        switch (id) {
            case 1:
                return PENDING;
            case 2:
                return DOING;
            case 3:
                return ERROR;
            case 4:
                return FINISH;
            case 5:
                return STOP;
            default:
                return NOT_STARTED;
        }
    }
}