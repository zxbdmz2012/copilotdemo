package com.github.copilot.task.strategy;

import com.github.copilot.task.entity.Node;
import com.github.copilot.task.entity.Task;

import java.util.List;



public class DefaultStrategy implements Strategy {

    @Override
    public boolean accept(List<Node> nodes, Task task, String myNodeId) {
        return true;
    }

}
