package com.github.copilot.schedule.enums;

import lombok.Getter;

/**
 * Enum for representing the status of nodes in a distributed system.
 * This enum is used to indicate whether a node is enabled or disabled for task execution.
 */
@Getter
public enum NodeStatus {

    // Indicates the node is disabled and not executing tasks.
    DISABLE(0),
    // Indicates the node is enabled and currently executing tasks.
    ENABLE(1);

    int id;

    NodeStatus(int id) {
        this.id = id;
    }

    public static NodeStatus valueOf(int id) {
        switch (id) {
            case 1:
                return ENABLE;
            default:
                return DISABLE;
        }
    }
}