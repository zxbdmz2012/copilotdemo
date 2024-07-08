package com.github.copilot.schedule.entity;

import com.github.copilot.schedule.enums.NodeStatus;
import com.github.copilot.schedule.enums.NotifyCmd;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

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

    private String nodeId;

    @Enumerated(EnumType.ORDINAL)
    private NodeStatus nodeStatus = NodeStatus.ENABLE;

    private Integer weight = 1;

    @Enumerated(EnumType.ORDINAL)
    private NotifyCmd notifyCmd = NotifyCmd.NO_NOTIFY;

    private String notifyValue;


}
