package com.github.copilot.configcenter.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.copilot.configcenter.server.model.ConfigBO;
import com.github.copilot.configcenter.common.model.ConfigVO;
import com.github.copilot.configcenter.common.model.Result;
import com.github.copilot.configcenter.entity.ConfigDO;
import com.github.copilot.configcenter.server.dao.ConfigDAO;
import com.github.copilot.configcenter.server.model.ConfigPolingTask;
import com.github.copilot.configcenter.server.model.ConfigPolingTasksHolder;
import com.github.copilot.configcenter.server.service.ConfigService;
import com.github.copilot.configcenter.server.service.ConfigSyncService;
import com.github.copilot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    private final ExecutorService respExecutor;
    private final ConfigPolingTasksHolder configPolingTasksHolder;
    @Autowired
    private ConfigDAO configDAO;
    @Autowired
    private ConfigSyncService configSyncService;
    @Autowired
    private ConfigSyncServiceImpl syncService;
    private int respThreadNum;

    public ConfigServiceImpl() {
        configPolingTasksHolder = new ConfigPolingTasksHolder();
        //构建用于响应长轮询的线程池
        respExecutor = new ThreadPoolExecutor(100, 5000,
                0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(102400),
                this::newRespThread,
                new ThreadPoolExecutor.CallerRunsPolicy());
        //每1秒轮询执行一次任务超时检测
        ScheduledExecutorService timeoutCheckExecutor = new ScheduledThreadPoolExecutor(1, this::newCheckThread);
        timeoutCheckExecutor.scheduleAtFixedRate(this::responseTimeoutTask, 0, 1, TimeUnit.SECONDS);
    }

    public static ConfigVO configBO2ConfigVO(ConfigBO configBO) {
        ConfigVO configVO = new ConfigVO();
        configVO.setId(configBO.getId());
        configVO.setName(configBO.getName());
        configVO.setVersion(configBO.getVersion());
        configVO.setConfigData(configBO.getConfigData());
        configVO.setCreateTime(DateUtil.date2str(configBO.getCreateTime()));
        return configVO;
    }

    @Override
    public Result<Void> insertConfig(ConfigBO configBO) {
        List<ConfigDO> allConfig = configDAO.getAllConfig();
        if (allConfig.stream().anyMatch(c -> c.getName().equals(configBO.getName()))) {
            return Result.fail("配置名重复");
        }
        ConfigDO configDO = new ConfigDO();
        configDO.setName(configBO.getName());
        configDO.setConfigData(configBO.getConfigData().toJSONString());
        configDAO.insertConfigDO(configDO);
        return Result.success(null);
    }

    @Override
    public Result<Void> updateConfig(ConfigBO configBO) {
        ConfigDO configDO = new ConfigDO();
        configDO.setId(configBO.getId());
        configDO.setName(configBO.getName());
        configDO.setConfigData(configBO.getConfigData().toJSONString());
        configDAO.updateConfig(configDO);
        configSyncService.publish(configBO.getId());
        return Result.success(null);
    }

    @Override
    public Result<Void> delConfig(long id) {
        configDAO.delConfig(id);
        return Result.success(null);
    }

    @Override
    public Result<List<ConfigBO>> getAllValidConfig() {
        List<ConfigDO> configList = configDAO.getAllConfig();
        return Result.success(configList.stream().map(this::ConfigDO2BO).collect(Collectors.toList()));
    }

    @Override
    public void configListener(ConfigPolingTask configPolingTask) {
        //先将任务加到待响应列表中，然后再判断账号是否有改变，防止并发问题
        //如先判断再加进去，加入前如有变动，任务里无法感知到，空等到超时
        configPolingTasksHolder.addConfigTask(configPolingTask);

        List<ConfigBO> allValidConfig = getAllValidConfig().getData();
        List<ConfigVO> changeConfigList = getChangeConfigList(configPolingTask, allValidConfig);
        if (!changeConfigList.isEmpty()) {
            List<ConfigPolingTask> todoTask = configPolingTasksHolder.getExecuteTaskList(configPolingTask::equals);
            if (!todoTask.isEmpty()) {
                doResponseTask(configPolingTask, Result.success(changeConfigList));
            }
        }
    }

    @Override
    public void onChangeConfigEvent(long configId) {
        List<ConfigPolingTask> todoTasks = configPolingTasksHolder.getExecuteTaskList(
                configPolingTask -> configPolingTask.getConfigPolingDataMap().containsKey(configId));

        if (!todoTasks.isEmpty()) {
            List<ConfigBO> configList = Collections.singletonList(ConfigDO2BO(configDAO.getConfig(configId)));
            todoTasks.forEach(todoTask -> {
                List<ConfigVO> changeConfigList = getChangeConfigList(todoTask, configList);
                respExecutor.submit(() -> doResponseTask(todoTask, Result.success(changeConfigList)));
            });
        }
    }

    private List<ConfigVO> getChangeConfigList(ConfigPolingTask configPolingTask, List<ConfigBO> configList) {
        Map<Long, Integer> configPolingDataMap = configPolingTask.getConfigPolingDataMap();
        return configList.stream()
                .filter(configBO -> configPolingDataMap.containsKey(configBO.getId()))
                .filter(configBO -> configBO.getVersion() > configPolingDataMap.get(configBO.getId()))
                .map(ConfigServiceImpl::configBO2ConfigVO).collect(Collectors.toList());
    }

    private ConfigBO ConfigDO2BO(ConfigDO configDO) {
        ConfigBO configBO = new ConfigBO();
        configBO.setId(configDO.getId());
        configBO.setName(configDO.getName());
        configBO.setVersion(configDO.getVersion());
        configBO.setCreateTime(configDO.getCreateTime());
        configBO.setConfigData(JSON.parseObject(configDO.getConfigData()));
        return configBO;
    }

    //响应超时未改变的任务
    private void responseTimeoutTask() {
        List<ConfigPolingTask> timeoutTasks = configPolingTasksHolder.getExecuteTaskList(
                configPolingTask -> System.currentTimeMillis() >= configPolingTask.getEndTime());

        timeoutTasks.forEach(timeoutTask -> respExecutor.submit(() ->
                doResponseTask(timeoutTask, Result.success(new ArrayList<>()))));
    }

    private void doResponseTask(ConfigPolingTask configPolingTask, Result<?> result) {
        AsyncContext asyncContext = configPolingTask.getAsyncContext();
        try (PrintWriter writer = asyncContext.getResponse().getWriter()) {
            writer.write(JSON.toJSONString(result));
            writer.flush();
        } catch (Exception e) {
            log.error("doResponseTimeoutTask error,task:{}", configPolingTask, e);
        } finally {
            asyncContext.complete();
        }
    }

    private Thread newCheckThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("ConfigLongPollingTimeoutCheckExecutor");
        return t;
    }

    private Thread newRespThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("ConfigLongPollingTimeoutRespExecutor-" + respThreadNum++);
        return t;
    }
}
