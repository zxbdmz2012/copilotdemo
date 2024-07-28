package com.github.copilot.configcenter.server.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Business Object class for configuration data.
 * This class is used to transfer configuration data within the application layers,
 * encapsulating the configuration details fetched from or to be persisted in the database.
 */
@Data
public class ConfigBO {

    /**
     * Unique identifier for the configuration.
     * This corresponds to the primary key in the database.
     */
    private long id;

    /**
     * Name of the configuration.
     * This is used to identify the configuration and is expected to be unique.
     */
    private String name;

    /**
     * Version number of the configuration.
     * Used for optimistic locking and to manage concurrent updates to the same configuration.
     */
    private int version;

    /**
     * The actual data of the configuration stored in JSON format.
     * This allows for flexible and structured storage of configuration parameters.
     */
    private JSONObject configData;

    /**
     * Timestamp indicating when the configuration was created.
     * Helps in tracking the age of the configuration and potentially in audit processes.
     */
    private Date createTime;
}