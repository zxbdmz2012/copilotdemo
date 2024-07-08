package com.github.copilot.configcenter.server.model;

import lombok.Data;

import javax.servlet.AsyncContext;
import java.util.Map;


@Data
public class ConfigPolingTask {
    /**
     * 截止时间
     */
    private long endTime;

    /**
     * 异步请求
     */
    private AsyncContext asyncContext;

    /**
     * 配置轮询数据（配置id，版本）
     */
    private Map<Long, Integer> configPolingDataMap;
}
