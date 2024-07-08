package com.github.copilot.schedule.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class EasyJobConfig {

    @Value("${easyjob.node.id:1}")
    private String nodeId;

    /**
     * 节点取任务的策略
     */
    @Value("${easyjob.node.strategy:default}")
    private String nodeStrategy;

    /**
     * 节点取任务的周期，单位是毫秒，默认100毫秒
     */
    @Value("${easyjob.node.fetchPeriod:100}")
    private int fetchPeriod;

    /**
     * 节点取任务据当前的时间段，比如每次取还有5分钟开始的任务，这里单位是秒
     */
    @Value("${easyjob.node.fetchDuration:300}")
    private int fetchDuration;

    /**
     * 线程池中队列大小
     */
    @Value("${easyjob.pool.queueSize:1000}")
    private int queueSize;

    /**
     * 线程池中初始线程数量
     */
    @Value("${easyjob.pool.coreSize:5}")
    private int corePoolSize;

    /**
     * 线程池中最大线程数量
     */
    @Value("${easyjob.pool.maxSize:10}")
    private int maxPoolSize;

    /**
     * 节点心跳周期，单位秒
     */
    @Value("${easyjob.heartBeat.seconds:20}")
    private int heartBeatSeconds;

    /**
     * 节点心跳开关，默认开
     */
    @Value("${easyjob.heartBeat.enable:true}")
    private boolean heartBeatEnable;

    /**
     * 恢复线程开关，默认开
     */
    @Value("${easyjob.recover.enable:true}")
    private boolean recoverEnable;

    /**
     * 恢复线程周期，默认60s
     */
    @Value("${easyjob.recover.seconds:60}")
    private int recoverSeconds;

//    @Bean(name = "easyjobDataSource")
//    @Qualifier("easyjobDataSource")
//    @ConfigurationProperties(prefix="easyjob.datasource")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }

    /**
     * 系统启动时间
     */
    private Date sysStartTime;


}
