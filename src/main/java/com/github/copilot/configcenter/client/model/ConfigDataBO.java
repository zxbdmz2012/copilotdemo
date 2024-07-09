package com.github.copilot.configcenter.client.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a single piece of configuration data, including its key, value, and any associated refresh fields.
 */
@Data
public class ConfigDataBO {
    /**
     * List of fields within beans that should be refreshed when the configuration data changes.
     */
    List<RefreshFieldBO> refreshFieldList;

    /**
     * The key identifying this piece of configuration data.
     */
    private String key;

    /**
     * The value of this piece of configuration data.
     */
    private String value;

    /**
     * Constructs a new ConfigDataBO with the specified key and value.
     * @param key The key for the configuration data.
     * @param value The value for the configuration data.
     */
    public ConfigDataBO(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Adds a field to be refreshed when the configuration data changes.
     * @param refreshFieldBO The field to add.
     */
    public void addRefreshField(RefreshFieldBO refreshFieldBO) {
        if (refreshFieldList == null) {
            refreshFieldList = new ArrayList<>();
        }
        refreshFieldList.add(refreshFieldBO);
    }
}