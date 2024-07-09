package com.github.copilot.configcenter.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Holds and manages a list of {@link ConfigPolingTask} instances.
 * This class is responsible for adding new polling tasks and retrieving tasks that meet certain conditions for execution.
 */
public class ConfigPolingTasksHolder {

    // List to hold ConfigPolingTask instances.
    private final List<ConfigPolingTask> configPolingTasks;

    /**
     * Constructor initializes the list of ConfigPolingTasks.
     */
    public ConfigPolingTasksHolder() {
        configPolingTasks = new ArrayList<>();
    }

    /**
     * Adds a new {@link ConfigPolingTask} to the list.
     * @param configPolingTask The ConfigPolingTask to add.
     */
    public synchronized void addConfigTask(ConfigPolingTask configPolingTask) {
        configPolingTasks.add(configPolingTask);
    }

    /**
     * Retrieves and removes tasks that satisfy the given predicate.
     * This method is synchronized to ensure thread safety during the modification of the list.
     * @param predicate A {@link Predicate} to test each ConfigPolingTask.
     * @return A list of ConfigPolingTasks that satisfy the predicate.
     */
    public synchronized List<ConfigPolingTask> getExecuteTaskList(Predicate<ConfigPolingTask> predicate) {
        List<ConfigPolingTask> resultTasks = new ArrayList<>();
        // Remove tasks that satisfy the predicate from configPolingTasks and add them to resultTasks.
        configPolingTasks.removeIf(configPolingTask -> {
            boolean res = predicate.test(configPolingTask);
            if (res) {
                resultTasks.add(configPolingTask);
            }
            return res;
        });
        return resultTasks;
    }
}