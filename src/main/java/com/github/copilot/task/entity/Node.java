package com.github.copilot.task.entity;

import com.github.copilot.db.BaseEntity;
import com.github.copilot.task.enums.NodeStatus;
import com.github.copilot.task.enums.NotifyCmd;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Table(name = "schedule_node")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Node extends BaseEntity {

    @Id
    private String nodeId;

    @Enumerated(EnumType.ORDINAL)
    private NodeStatus nodeStatus = NodeStatus.ENABLE;

    private Integer weight = 1;

    @Enumerated(EnumType.ORDINAL)
    private NotifyCmd notifyCmd = NotifyCmd.NO_NOTIFY;

    private String notifyValue;

    public Node(String nodeId) {
        this.nodeId = nodeId;
    }
}
