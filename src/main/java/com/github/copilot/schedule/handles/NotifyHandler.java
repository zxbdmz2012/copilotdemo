package com.github.copilot.schedule.handles;

import com.github.copilot.schedule.enums.NotifyCmd;
import com.github.copilot.util.SpringContextUtil;

/**
 * Interface for handling notifications related to task operations.
 * It defines a method for updating task status based on a notification command.
 * The implementation of this interface should handle specific task operations like start, stop, or edit.
 */
public interface NotifyHandler<T> {

    /**
     * Chooses the appropriate handler based on the notification command.
     *
     * @param notifyCmd The notification command.
     * @return The handler corresponding to the notification command.
     */
    static NotifyHandler chooseHandler(NotifyCmd notifyCmd) {
        return SpringContextUtil.getByTypeAndName(NotifyHandler.class, notifyCmd.toString());
    }

    /**
     * Updates the task status or information based on the provided data.
     *
     * @param t The data used for updating the task.
     */
    void update(T t);
}