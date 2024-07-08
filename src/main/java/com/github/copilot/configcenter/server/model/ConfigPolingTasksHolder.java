package com.github.copilot.configcenter.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class ConfigPolingTasksHolder {

    private final List<ConfigPolingTask> configPolingTasks;

    public ConfigPolingTasksHolder() {
        configPolingTasks = new ArrayList<>();
    }

    public synchronized void addConfigTask(ConfigPolingTask configPolingTask) {
        configPolingTasks.add(configPolingTask);
    }

    //将要处理的任务在任务列表中删除，并将其放到外面执行，防止锁的时间太长
    public synchronized List<ConfigPolingTask> getExecuteTaskList(Predicate<ConfigPolingTask> predicate) {
        List<ConfigPolingTask> resultTasks = new ArrayList<>();
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
