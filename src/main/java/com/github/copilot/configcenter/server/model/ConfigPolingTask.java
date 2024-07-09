package com.github.copilot.configcenter.server.model;

import lombok.Data;

import javax.servlet.AsyncContext;
import java.util.Map;

/**
 * Represents a task for polling configuration changes.
 * This class encapsulates the necessary details for performing long polling operations
 * to detect changes in configuration data.
 */
@Data
public class ConfigPolingTask {
    /**
     * The end time for the polling operation.
     * This is used to determine when the polling should stop.
     */
    private long endTime;

    /**
     * The asynchronous context associated with the servlet request.
     * This allows the polling operation to be performed asynchronously, freeing up server resources.
     */
    private AsyncContext asyncContext;

    /**
     * A map containing configuration IDs and their respective versions.
     * This is used to track changes to configurations and determine if an update has occurred.
     */
    private Map<Long, Integer> configPolingDataMap;
}