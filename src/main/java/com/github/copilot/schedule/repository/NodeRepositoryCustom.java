package com.github.copilot.schedule.repository;


import com.github.copilot.schedule.entity.Node;

import java.util.List;

public interface NodeRepositoryCustom {
    List<Node> getEnableNodes(int timeout);
}