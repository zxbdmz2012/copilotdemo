package com.github.copilot.configcenter.server.service.impl;

// Import statements for required services and annotations
import com.github.copilot.configcenter.server.service.ConfigService;
import com.github.copilot.configcenter.server.service.ConfigSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ConfigSyncService interface.
 * This service is responsible for publishing configuration changes and consuming them,
 * effectively triggering the onChangeConfigEvent method of the ConfigService.
 */
@Service
public class ConfigSyncServiceImpl implements ConfigSyncService {

    // Autowired dependency on ConfigService to delegate the call to onChangeConfigEvent
    @Autowired
    private ConfigService configService;

    /**
     * Publishes a configuration change event.
     * This method is intended to be called when a configuration change occurs,
     * and it delegates to the consume method to handle the change.
     *
     * @param configId The ID of the configuration that has changed.
     */
    @Override
    public void publish(long configId) {
        consume(configId);
    }

    /**
     * Consumes a configuration change event.
     * This method is called by the publish method and triggers the onChangeConfigEvent
     * method of the ConfigService to handle the configuration change.
     *
     * @param configId The ID of the configuration that has changed.
     */
    @Override
    public void consume(long configId) {
        // Trigger the onChangeConfigEvent method in ConfigService with the given configId
        configService.onChangeConfigEvent(configId);
    }
}