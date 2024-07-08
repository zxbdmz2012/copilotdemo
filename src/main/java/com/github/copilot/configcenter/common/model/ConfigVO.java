package com.github.copilot.configcenter.common.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;


@Data
public class ConfigVO {
    /**
     * 配置id
     */
    private long id;

    /**
     * 配置名
     */
    private String name;

    /**
     * 配置版本号
     */
    private int version;

    /**
     * 配置内容
     */
    private JSONObject configData;

    /**
     * 创建时间
     */
    private String createTime;
}
