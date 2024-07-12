package com.github.copilot.task.strategy;

import com.github.copilot.task.entity.Node;
import com.github.copilot.task.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a strategy for task distribution based on node weights.
 * This strategy selects tasks for execution based on the weight assigned to each node,
 * ensuring that nodes with higher weights have a higher chance of receiving tasks.
 */
public class WeightStrategy implements Strategy {

    /**
     * Determines if the current node should accept the given task based on the node's weight.
     *
     * @param nodes The list of all nodes in the system.
     * @param task The task to be distributed.
     * @param myNodeId The ID of the current node.
     * @return true if the current node should accept the task, false otherwise.
     */
    @Override
    public boolean accept(List<Node> nodes, Task task, String myNodeId) {
        // Find the current node in the list of nodes.
        Node myNode = nodes.stream().filter(node -> node.getNodeId().equals(myNodeId)).findFirst().orElse(null);
        if (myNode == null) {
            return false;
        }
        // Get the index of the current node.
        int myNodeIndex = nodes.indexOf(myNode);
        // Calculate the sum of weights for all nodes before the current node.
        int preWeightSum = nodes.subList(0, myNodeIndex).stream().collect(Collectors.summingInt(Node::getWeight));
        // Calculate the total sum of weights for all nodes.
        int weightSum = nodes.stream().collect(Collectors.summingInt(Node::getWeight));
        // Determine the task's position based on its ID and the total weight sum.
        int remainder = (int) (task.getId() % weightSum);
        // Check if the remainder falls within the weight range of the current node.
        return remainder >= preWeightSum && remainder < preWeightSum + myNode.getWeight();
    }
}