package com.github.copilot.schedule.enums;

import lombok.Getter;

/**
 * Enum for representing commands related to task notifications.
 * This enum defines various commands that can be sent to notify about task operations.
 */
@Getter
public enum NotifyCmd {

    // Represents the default state with no notification.
    NO_NOTIFY(0),
    // Represents a command to start a task.
    START_TASK(1),
    // Represents a command to edit a task.
    EDIT_TASK(2),
    // Represents a command to stop a task.
    STOP_TASK(3);

    int id;

    NotifyCmd(int id) {
        this.id = id;
    }

    public static NotifyCmd valueOf(int id) {
        switch (id) {
            case 1:
                return START_TASK;
            case 2:
                return EDIT_TASK;
            case 3:
                return STOP_TASK;
            default:
                return NO_NOTIFY;
        }
    }
}