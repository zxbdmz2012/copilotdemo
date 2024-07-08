package com.github.copilot.schedule.repository;

import com.github.copilot.schedule.entity.Node;
import com.github.copilot.schedule.enums.NodeStatus;
import com.github.copilot.schedule.enums.NotifyCmd;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 任务对象数据库操作对象
 */
@Component
public class NodeRepository {


    @Resource
    private NodeJpaRepository nodeJpaRepository;

    public Node insert(Node node) {
        return nodeJpaRepository.save(node);
    }

    /**
     * 更新节点心跳时间和序号
     *
     * @param nodeId 待更新节点ID
     * @return
     * @throws Exception
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
     * 更新节点的通知信息,实现修改任务，停止任务通知等
     *
     * @param cmd         通知指令
     * @param notifyValue 通知的值，一般存id
     * @return
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
     * 当通知执行完后使用乐观锁重置通知信息
     *
     * @param cmd
     * @return
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
     * 禁用节点
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

    public int enable(Node nodeEnabled) {
        Node node = nodeJpaRepository.findByNodeId(nodeEnabled.getNodeId());
        if (Objects.nonNull(node)) {
            node.setNodeStatus(NodeStatus.ENABLE);
            nodeJpaRepository.save(node);
            return 1;
        }
        return 0;
    }

    public List<Node> getEnableNodes(int timeout) {
        return nodeJpaRepository.findByNodeStatusAndUpdateTimeBefore(NodeStatus.ENABLE, LocalDateTime.now().minusSeconds(timeout));
    }

    public Node getByNodeId(String nodeId) {
        return nodeJpaRepository.findByNodeId(nodeId);
    }


}
