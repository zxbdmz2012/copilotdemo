package com.github.copilot.schedule.strategy;

import com.github.copilot.schedule.entity.Node;
import com.github.copilot.schedule.entity.Task;

import java.util.List;


/**
 * 抽象的策略接口
 */
public interface Strategy {

    /**
     * 默认策略
     */
    String DEFAULT = "default";

    /**
     * 按节点权重
     */
    String WEIGHT = "weight";


    static Strategy choose(String key) {
        switch (key) {
            case WEIGHT:
                return new WeightStrategy();
            default:
                return new DefaultStrategy();
        }
    }

    boolean accept(List<Node> nodes, Task task, String myNodeId);

}
