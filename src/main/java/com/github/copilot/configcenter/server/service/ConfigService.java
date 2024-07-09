package com.github.copilot.configcenter.server.service;

import com.github.copilot.configcenter.common.model.Result;
import com.github.copilot.configcenter.server.model.ConfigBO;
import com.github.copilot.configcenter.server.model.ConfigPolingTask;

import java.util.List;

/**
 * Defines the contract for configuration management services.
 * This interface outlines the operations for managing configuration data,
 * including CRUD operations and listening for configuration changes.
 */
public interface ConfigService {

    /**
     * Adds a new configuration.
     * This method is responsible for inserting a new configuration into the system.
     *
     * @param configBO The configuration object containing the data to be inserted.
     * @return A Result object indicating the outcome of the operation.
     */
    Result<Void> insertConfig(ConfigBO configBO);

    /**
     * Updates an existing configuration.
     * This method updates the details of an existing configuration based on the provided data.
     *
     * @param configBO The configuration object containing the updated data.
     * @return A Result object indicating the outcome of the operation.
     */
    Result<Void> updateConfig(ConfigBO configBO);

    /**
     * Deletes a configuration by its ID.
     * This method removes a configuration from the system based on its unique identifier.
     *
     * @param id The unique identifier of the configuration to be deleted.
     * @return A Result object indicating the outcome of the operation.
     */
    Result<Void> delConfig(long id);

    /**
     * Retrieves all valid configurations.
     * This method fetches a list of all configurations that are currently valid and active.
     *
     * @return A Result object containing a list of ConfigBO objects representing the valid configurations.
     */
    Result<List<ConfigBO>> getAllValidConfig();

    /**
     * Listens for configuration changes.
     * This method is responsible for handling long polling requests to detect configuration changes.
     *
     * @param configPolingTask The polling task containing the details of the request for configuration changes.
     */
    void configListener(ConfigPolingTask configPolingTask);

    /**
     * Handles tasks after a configuration change event.
     * This method is called to process any necessary actions after a configuration has been changed.
     *
     * @param configId The unique identifier of the configuration that has changed.
     */
    void onChangeConfigEvent(long configId);
}