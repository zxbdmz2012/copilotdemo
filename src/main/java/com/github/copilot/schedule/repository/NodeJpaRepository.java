package com.github.copilot.schedule.repository;

import com.github.copilot.schedule.entity.Node;
import com.github.copilot.schedule.enums.NodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NodeJpaRepository extends JpaRepository<Node, String> {

    Node findByNodeId(String nodeId);

    List<Node> findByNodeStatusAndUpdateTimeBefore(NodeStatus nodeStatus, LocalDateTime localDateTime);
}
