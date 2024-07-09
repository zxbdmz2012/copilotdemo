package com.github.copilot.configcenter.common.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * Represents the configuration object with details about a specific configuration.
 */
@Data
public class ConfigVO {
    /**
     * Unique identifier for the configuration.
     */
    private long id;

    /**
     * Name of the configuration.
     */
    private String name;

    /**
     * Version number of the configuration, used for tracking changes.
     */
    private int version;

    /**
     * The actual data of the configuration stored as a JSON object.
     * This allows for a flexible and structured format to store configuration parameters.
     */
    private JSONObject configData;

    /**
     * The creation time of the configuration.
     * This could be used for auditing or tracking the age of the configuration.
     */
    private String createTime;
}