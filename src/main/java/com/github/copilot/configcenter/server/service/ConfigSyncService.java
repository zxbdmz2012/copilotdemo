package com.github.copilot.configcenter.server.service;

/**
 * Interface defining the contract for synchronization of configuration changes.
 * This service is responsible for publishing configuration change events and consuming them,
 * ensuring that any changes are propagated throughout the system.
 */
public interface ConfigSyncService {

    /**
     * Publishes a configuration change event.
     * This method should be called to notify the system of a change in a specific configuration.
     * It acts as a trigger for any further processing or handling that needs to occur as a result of the change.
     *
     * @param configId The unique identifier of the configuration that has changed.
     */
    void publish(long configId);

    /**
     * Consumes a configuration change event.
     * This method is responsible for handling the actual change event, typically by invoking
     * the appropriate services or components that need to react to the change.
     *
     * @param configId The unique identifier of the configuration that has changed.
     */
    void consume(long configId);
}