package com.github.copilot.task.strategy;

import com.github.copilot.task.entity.Node;
import com.github.copilot.task.entity.Task;

import java.util.List;


/**
 * 默认的来者不惧的策略，只要能抢到就要，其实这种方式等同于随机分配任务，因为谁可以抢到不一定
 */
public class DefaultStrategy implements Strategy {

    @Override
    public boolean accept(List<Node> nodes, Task task, String myNodeId) {
        return true;
    }

}
