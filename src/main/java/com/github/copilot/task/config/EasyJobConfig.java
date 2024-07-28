package com.github.copilot.task.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Configuration class for EasyJob scheduling tasks.
 * This class holds the configuration properties for the EasyJob scheduler, including node identification,
 * task fetching strategies, thread pool configurations, and heartbeat settings. These configurations
 * are loaded from application properties and can be overridden by specifying them in the application's
 * configuration file.
 */
@Component
@Data
public class EasyJobConfig implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(EasyJobConfig.class);
    private String nodeId;

    /**
     * Strategy for how nodes fetch tasks. Can be overridden in application properties.
     */
    @Value("${easyjob.node.strategy:default}")
    private String nodeStrategy;

    /**
     * The period in milliseconds for how often nodes fetch tasks. Default is 100ms.
     */
    @Value("${easyjob.node.fetchPeriod:100}")
    private int fetchPeriod;

    /**
     * The duration in seconds for fetching tasks ahead of their scheduled time. Default is 300 seconds (5 minutes).
     */
    @Value("${easyjob.node.fetchDuration:300}")
    private int fetchDuration;

    /**
     * The size of the queue in the thread pool. Default is 1000.
     */
    @Value("${easyjob.pool.queueSize:1000}")
    private int queueSize;

    /**
     * The initial number of threads in the thread pool. Default is 5.
     */
    @Value("${easyjob.pool.coreSize:5}")
    private int corePoolSize;

    /**
     * The maximum number of threads in the thread pool. Default is 10.
     */
    @Value("${easyjob.pool.maxSize:10}")
    private int maxPoolSize;

    /**
     * The period in seconds for node heartbeat signals. Default is 20 seconds.
     */
    @Value("${easyjob.heartBeat.seconds:20}")
    private int heartBeatSeconds;

    /**
     * Whether the node heartbeat mechanism is enabled. Default is false.
     */
    @Value("${easyjob.heartBeat.enable:false}")
    private boolean heartBeatEnable;

    /**
     * Whether the recovery mechanism is enabled for handling failed tasks. Default is false.
     */
    @Value("${easyjob.recover.enable:false}")
    private boolean recoverEnable;

    /**
     * The period in seconds for the recovery mechanism to run. Default is 60 seconds.
     */
    @Value("${easyjob.recover.seconds:60}")
    private int recoverSeconds;

    /**
     * Retrieves the node ID, which is the local IP address of the machine. If the IP address cannot be determined,
     * it logs an error and returns "unknown".
     *
     * @return The node ID as a string.
     */
    public String getNodeId() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // Log the error or return a default value
            log.error("Unable to get local IP address: " + e.getMessage());
            return "unknown";
        }
    }

    /**
     * The system start time, used for monitoring and possibly for recovery mechanisms.
     */
    private Date sysStartTime;

    public Date getSystStartTime() {
        if (sysStartTime == null) {
            sysStartTime = new Date();
        }
        return sysStartTime;
    }

    @PostConstruct
    public void init() {
        log.info("EasyJob configuration initialized");
        sysStartTime = new Date();
        log.info("System start time: {}", sysStartTime);
        log.info("Node ID: {}", nodeId);

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Recovery mechanism enabled: {}", recoverEnable);
        log.info("Heartbeat mechanism enabled: {}", heartBeatEnable);
    }
}