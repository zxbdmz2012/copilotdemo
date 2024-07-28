package com.github.copilot.task.repository;

import com.github.copilot.task.entity.Node;
import com.github.copilot.task.enums.NodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface NodeJpaRepository extends JpaRepository<Node, String> {

    Node findByNodeId(String nodeId);

    List<Node> findByNodeStatusAndUpdateTimeAfter(NodeStatus nodeStatus, Date date);
}
