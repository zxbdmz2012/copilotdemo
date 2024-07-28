package com.github.copilot.task.strategy;

import com.github.copilot.task.entity.Node;
import com.github.copilot.task.entity.Task;

import java.util.List;


public interface Strategy {

    String DEFAULT = "default";


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
