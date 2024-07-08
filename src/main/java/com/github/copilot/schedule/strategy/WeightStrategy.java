package com.github.copilot.schedule.strategy;

import com.github.copilot.schedule.entity.Node;
import com.github.copilot.schedule.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

public class WeightStrategy implements Strategy {

    @Override
    public boolean accept(List<Node> nodes, Task task, String myNodeId) {
        // 找到当前节点
        Node myNode = nodes.stream().filter(node -> node.getNodeId().equals(myNodeId)).findFirst().orElse(null);
        if (myNode == null) {
            return false;
        }
        // 获取当前节点的索引
        int myNodeIndex = nodes.indexOf(myNode);
        // 计算当前节点之前所有节点的权重总和
        int preWeightSum = nodes.subList(0, myNodeIndex).stream().collect(Collectors.summingInt(Node::getWeight));
        // 计算所有节点的权重总和
        int weightSum = nodes.stream().collect(Collectors.summingInt(Node::getWeight));
        // 根据任务ID对权重总和取余，得到余数
        int remainder = (int) (task.getId() % weightSum);
        // 判断余数是否在当前节点的权重范围内
        return remainder >= preWeightSum && remainder < preWeightSum + myNode.getWeight();
    }
}