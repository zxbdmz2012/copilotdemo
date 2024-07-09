package com.github.copilot.configcenter.server.service;


import com.github.copilot.configcenter.common.model.Result;
import com.github.copilot.configcenter.server.model.ConfigBO;
import com.github.copilot.configcenter.server.model.ConfigPolingTask;

import java.util.List;


public interface ConfigService {

    /**
     * 新增配置文件
     *
     * @param configBO 配置参数
     * @return Result
     */
    Result<Void> insertConfig(ConfigBO configBO);

    /**
     * 更新配置文件
     *
     * @param configBO 配置参数
     * @return Result
     */
    Result<Void> updateConfig(ConfigBO configBO);

    /**
     * 更新配置文件
     *
     * @param id 配置id
     * @return Result
     */
    Result<Void> delConfig(long id);

    /**
     * 获取所有配置文件
     *
     * @return Result
     */
    Result<List<ConfigBO>> getAllValidConfig();

    /**
     * 监听配置变动
     *
     * @param configPolingTask 配置轮询任务
     */
    void configListener(ConfigPolingTask configPolingTask);

    /**
     * 配置变动后处理对应任务
     *
     * @param configId 配置id
     */
    void onChangeConfigEvent(long configId);
}
