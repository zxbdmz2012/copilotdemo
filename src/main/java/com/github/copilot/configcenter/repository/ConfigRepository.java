package com.github.copilot.configcenter.repository;


import com.github.copilot.configcenter.entity.ConfigDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ConfigRepository extends JpaRepository<ConfigDO, Long> {
    List<ConfigDO> findByUpdateTimeAfter(Date updateTime);
}
