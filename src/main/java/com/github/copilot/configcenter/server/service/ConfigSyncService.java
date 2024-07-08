package com.github.copilot.configcenter.server.service;


public interface ConfigSyncService {

    /**
     * 发布事件
     *
     * @param configId 配置id
     */
    void publish(long configId);

    /**
     * 消费事件
     *
     * @param configId 配置id
     */
    void consume(long configId);
}
