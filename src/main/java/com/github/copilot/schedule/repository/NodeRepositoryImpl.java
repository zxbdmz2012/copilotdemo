package com.github.copilot.schedule.repository;


import com.github.copilot.schedule.entity.Node;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class NodeRepositoryImpl implements NodeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Node> getEnableNodes(int timeout) {
        String jpql = "SELECT n FROM Node n WHERE " +
                "n.modified_date > (localtimestamp - interval :timeout second))" +
                "and n .status = 1 ORDER BY n.nodeId";
        Query query = entityManager.createQuery(jpql, Node.class);
        query.setParameter("timeout", timeout);
        return query.getResultList();
    }
}