package com.github.copilot.configcenter.client.model;

import lombok.Data;

import java.util.List;

/**
 * Represents a configuration object with an ID, version, and a list of configuration data objects.
 */
@Data
public class ConfigBO {
    /**
     * Unique identifier for the configuration.
     */
    private long id;

    /**
     * Version number of the configuration.
     */
    private int version;

    /**
     * List of configuration data objects associated with this configuration.
     */
    private List<ConfigDataBO> configDataList;
}