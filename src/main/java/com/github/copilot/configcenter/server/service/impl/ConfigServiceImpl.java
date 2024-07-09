package com.github.copilot.configcenter.server.service.impl;

// Required imports for the service implementation
import com.alibaba.fastjson.JSON;
import com.github.copilot.configcenter.common.model.ConfigVO;
import com.github.copilot.configcenter.common.model.Result;
import com.github.copilot.configcenter.entity.ConfigDO;
import com.github.copilot.configcenter.server.annotation.EnableConfigServer;
import com.github.copilot.configcenter.server.dao.ConfigDAO;
import com.github.copilot.configcenter.server.model.ConfigBO;
import com.github.copilot.configcenter.server.model.ConfigPolingTask;
import com.github.copilot.configcenter.server.model.ConfigPolingTasksHolder;
import com.github.copilot.configcenter.server.service.ConfigService;
import com.github.copilot.configcenter.server.service.ConfigSyncService;
import com.github.copilot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

// Log annotation for logging support
@Slf4j
// Marks this class as a Spring service component
@Service
// Implements the ConfigService interface
public class ConfigServiceImpl implements ConfigService {

    // Executor service for handling asynchronous response tasks
    private ExecutorService respExecutor;
    // Holder for managing configuration polling tasks
    private ConfigPolingTasksHolder configPolingTasksHolder;
    // DAO for accessing configuration data
    @Autowired
    private ConfigDAO configDAO;
    // Service for synchronizing configuration updates
    @Autowired
    private ConfigSyncService configSyncService;
    // Implementation of the configuration synchronization service
    @Autowired
    private ConfigSyncServiceImpl syncService;
    // Number of threads for the response executor
    private int respThreadNum;

    // Constructor to initialize the service
    public ConfigServiceImpl(ApplicationContext applicationContext) {
        // Check if the configuration server is enabled
        boolean isConfigServerEnabled = !applicationContext.getBeansWithAnnotation(EnableConfigServer.class).isEmpty();
        if (isConfigServerEnabled) {
            // Initialize the polling tasks holder
            configPolingTasksHolder = new ConfigPolingTasksHolder();
            // Initialize the executor service for handling response tasks
            respExecutor = new ThreadPoolExecutor(100, 5000,
                    0, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(102400),
                    this::newRespThread,
                    new ThreadPoolExecutor.CallerRunsPolicy());
            // Scheduled executor for checking task timeouts
            ScheduledExecutorService timeoutCheckExecutor = new ScheduledThreadPoolExecutor(1, this::newCheckThread);
            // Schedule the timeout check task to run every second
            timeoutCheckExecutor.scheduleAtFixedRate(this::responseTimeoutTask, 0, 1, TimeUnit.SECONDS);

        }

    }

    // Converts a ConfigBO to a ConfigVO
    public static ConfigVO configBO2ConfigVO(ConfigBO configBO) {
        // Create a new ConfigVO instance
        ConfigVO configVO = new ConfigVO();
        // Set properties from the ConfigBO to the ConfigVO
        configVO.setId(configBO.getId());
        configVO.setName(configBO.getName());
        configVO.setVersion(configBO.getVersion());
        configVO.setConfigData(configBO.getConfigData());
        // Convert the creation time to string format
        configVO.setCreateTime(DateUtil.date2str(configBO.getCreateTime()));
        return configVO;
    }

    // Inserts a new configuration
    @Override
    public Result<Void> insertConfig(ConfigBO configBO) {
        // Retrieve all configurations
        List<ConfigDO> allConfig = configDAO.getAllConfig();
        // Check for duplicate configuration names
        if (allConfig.stream().anyMatch(c -> c.getName().equals(configBO.getName()))) {
            return Result.fail("配置名重复");
        }
        // Create a new ConfigDO for insertion
        ConfigDO configDO = new ConfigDO();
        configDO.setName(configBO.getName());
        configDO.setConfigData(configBO.getConfigData().toJSONString());
        // Insert the new configuration
        configDAO.insertConfigDO(configDO);
        return Result.success(null);
    }

    // Updates an existing configuration
    @Override
    public Result<Void> updateConfig(ConfigBO configBO) {
        // Create a new ConfigDO for updating
        ConfigDO configDO = new ConfigDO();
        configDO.setId(configBO.getId());
        configDO.setName(configBO.getName());
        configDO.setConfigData(configBO.getConfigData().toJSONString());
        // Update the configuration
        configDAO.updateConfig(configDO);
        // Publish the update event
        configSyncService.publish(configBO.getId());
        return Result.success(null);
    }

    // Deletes a configuration by ID
    @Override
    public Result<Void> delConfig(long id) {
        // Delete the configuration
        configDAO.delConfig(id);
        return Result.success(null);
    }

    // Retrieves all valid configurations
    @Override
    public Result<List<ConfigBO>> getAllValidConfig() {
        // Retrieve all configurations
        List<ConfigDO> configList = configDAO.getAllConfig();
        // Convert ConfigDOs to ConfigBOs and return
        return Result.success(configList.stream().map(this::ConfigDO2BO).collect(Collectors.toList()));
    }

    // Listens for configuration changes and responds to polling tasks
    @Override
    public void configListener(ConfigPolingTask configPolingTask) {
        // Add the task to the polling tasks holder
        configPolingTasksHolder.addConfigTask(configPolingTask);

        // Retrieve all valid configurations
        List<ConfigBO> allValidConfig = getAllValidConfig().getData();
        // Determine if there are any changes relevant to the polling task
        List<ConfigVO> changeConfigList = getChangeConfigList(configPolingTask, allValidConfig);
        if (!changeConfigList.isEmpty()) {
            // Retrieve tasks to be executed
            List<ConfigPolingTask> todoTask = configPolingTasksHolder.getExecuteTaskList(configPolingTask::equals);
            if (!todoTask.isEmpty()) {
                // Respond to the polling task
                doResponseTask(configPolingTask, Result.success(changeConfigList));
            }
        }
    }

    // Handles configuration change events
    @Override
    public void onChangeConfigEvent(long configId) {
        // Retrieve tasks affected by the configuration change
        List<ConfigPolingTask> todoTasks = configPolingTasksHolder.getExecuteTaskList(
                configPolingTask -> configPolingTask.getConfigPolingDataMap().containsKey(configId));

        if (!todoTasks.isEmpty()) {
            // Retrieve the updated configuration
            List<ConfigBO> configList = Collections.singletonList(ConfigDO2BO(configDAO.getConfig(configId)));
            // Respond to each affected polling task
            todoTasks.forEach(todoTask -> {
                List<ConfigVO> changeConfigList = getChangeConfigList(todoTask, configList);
                respExecutor.submit(() -> doResponseTask(todoTask, Result.success(changeConfigList)));
            });
        }
    }

    // Determines if there are any changes to configurations relevant to a polling task
    private List<ConfigVO> getChangeConfigList(ConfigPolingTask configPolingTask, List<ConfigBO> configList) {
        // Retrieve the polling data map from the task
        Map<Long, Integer> configPolingDataMap = configPolingTask.getConfigPolingDataMap();
        // Filter configurations for changes and convert to ConfigVOs
        return configList.stream()
                .filter(configBO -> configPolingDataMap.containsKey(configBO.getId()))
                .filter(configBO -> configBO.getVersion() > configPolingDataMap.get(configBO.getId()))
                .map(ConfigServiceImpl::configBO2ConfigVO).collect(Collectors.toList());
    }

    // Converts a ConfigDO to a ConfigBO
    private ConfigBO ConfigDO2BO(ConfigDO configDO) {
        // Create a new ConfigBO instance
        ConfigBO configBO = new ConfigBO();
        // Set properties from the ConfigDO to the ConfigBO
        configBO.setId(configDO.getId());
        configBO.setName(configDO.getName());
        configBO.setVersion(configDO.getVersion());
        configBO.setCreateTime(configDO.getCreateTime());
        configBO.setConfigData(JSON.parseObject(configDO.getConfigData()));
        return configBO;
    }

    // Handles tasks that have timed out without a configuration change
    private void responseTimeoutTask() {
        // Retrieve tasks that have timed out
        List<ConfigPolingTask> timeoutTasks = configPolingTasksHolder.getExecuteTaskList(
                configPolingTask -> System.currentTimeMillis() >= configPolingTask.getEndTime());

        // Respond to each timed-out task
        timeoutTasks.forEach(timeoutTask -> respExecutor.submit(() ->
                doResponseTask(timeoutTask, Result.success(new ArrayList<>()))));
    }

    // Responds to a polling task with the result
    private void doResponseTask(ConfigPolingTask configPolingTask, Result<?> result) {
        // Retrieve the asynchronous context from the polling task
        AsyncContext asyncContext = configPolingTask.getAsyncContext();
        try (PrintWriter writer = asyncContext.getResponse().getWriter()) {
            // Write the result to the response
            writer.write(JSON.toJSONString(result));
            writer.flush();
        } catch (Exception e) {
            // Log any errors that occur during the response
            log.error("doResponseTimeoutTask error,task:{}", configPolingTask, e);
        } finally {
            // Complete the asynchronous context
            asyncContext.complete();
        }
    }

    // Creates a new thread for checking task timeouts
    private Thread newCheckThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("ConfigLongPollingTimeoutCheckExecutor");
        return t;
    }

    // Creates a new thread for responding to tasks
    private Thread newRespThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        // Increment and set the thread name for response executor threads
        t.setName("ConfigLongPollingTimeoutRespExecutor-" + respThreadNum++);
        return t;
    }
}