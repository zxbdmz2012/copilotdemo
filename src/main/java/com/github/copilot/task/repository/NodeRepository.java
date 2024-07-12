package com.github.copilot.task.repository;

import com.github.copilot.task.entity.Node;
import com.github.copilot.task.enums.NodeStatus;
import com.github.copilot.task.enums.NotifyCmd;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Repository component for managing {@link Node} entities.
 * Provides methods for inserting, updating, and querying node entities in the database.
 * It includes operations for updating node heartbeat, handling notifications, enabling/disabling nodes,
 * and retrieving nodes based on their status and update time.
 */
@Component
public class NodeRepository {

    @Resource
    private NodeJpaRepository nodeJpaRepository;

    /**
     * Inserts a new node entity into the database.
     *
     * @param node The node entity to insert.
     * @return The inserted node entity.
     */
    public Node insert(Node node) {
        return nodeJpaRepository.save(node);
    }

    /**
     * Updates the heartbeat timestamp of a node.
     *
     * @param nodeId The ID of the node to update.
     * @return The updated node entity, or null if the node does not exist.
     */
    public Node updateHeartBeat(String nodeId) {
        Node node = nodeJpaRepository.findByNodeId(nodeId);
        if (Objects.nonNull(node)) {
            node.setUpdateTime(LocalDateTime.now());
            return nodeJpaRepository.save(node);
        }
        return null;
    }

    /**
     * Updates the notification command and value for a node.
     *
     * @param nodeId The ID of the node to update.
     * @param cmd The notification command to set.
     * @param notifyValue The notification value to set.
     * @return 1 if the update was successful, 0 otherwise.
     */
    public int updateNotifyInfo(String nodeId, NotifyCmd cmd, String notifyValue) {
        Node node = nodeJpaRepository.findByNodeId(nodeId);
        if (Objects.nonNull(node)) {
            node.setNotifyCmd(cmd);
            node.setNotifyValue(notifyValue);
            nodeJpaRepository.save(node);
            return 1;
        }
        return 0;
    }

    /**
     * Resets the notification information for a node using optimistic locking.
     *
     * @param nodeId The ID of the node to reset.
     * @param cmd The notification command to reset.
     * @return 1 if the reset was successful, 0 otherwise.
     */
    public int resetNotifyInfo(String nodeId, NotifyCmd cmd) {
        Node node = nodeJpaRepository.findByNodeId(nodeId);
        if (Objects.nonNull(node)) {
            node.setNotifyCmd(NotifyCmd.NO_NOTIFY);
            node.setNotifyValue("");
            nodeJpaRepository.save(node);
            return 1;
        }
        return 0;
    }

    /**
     * Disables a node, preventing it from executing tasks.
     *
     * @param nodeDisabled The node to disable.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int disbale(Node nodeDisabled) {
        Node node = nodeJpaRepository.findByNodeId(nodeDisabled.getNodeId());
        if (Objects.nonNull(node)) {
            node.setNodeStatus(NodeStatus.DISABLE);
            nodeJpaRepository.save(node);
            return 1;
        }
        return 0;
    }

    /**
     * Enables a node, allowing it to execute tasks.
     *
     * @param nodeEnabled The node to enable.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int enable(Node nodeEnabled) {
        Node node = nodeJpaRepository.findByNodeId(nodeEnabled.getNodeId());
        if (Objects.nonNull(node)) {
            node.setNodeStatus(NodeStatus.ENABLE);
            nodeJpaRepository.save(node);
            return 1;
        }
        return 0;
    }

    /**
     * Retrieves a list of enabled nodes that have not updated their status within a specified timeout period.
     *
     * @param timeout The timeout period in seconds.
     * @return A list of enabled nodes.
     */
    public List<Node> getEnableNodes(int timeout) {
        return nodeJpaRepository.findByNodeStatusAndUpdateTimeBefore(NodeStatus.ENABLE, LocalDateTime.now().minusSeconds(timeout));
    }

    /**
     * Retrieves a node by its ID.
     *
     * @param nodeId The ID of the node to retrieve.
     * @return The node entity, or null if not found.
     */
    public Node getByNodeId(String nodeId) {
        return nodeJpaRepository.findByNodeId(nodeId);
    }
}