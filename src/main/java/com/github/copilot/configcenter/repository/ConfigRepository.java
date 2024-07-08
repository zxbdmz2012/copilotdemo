package com.github.copilot.configcenter.repository;


import com.github.copilot.configcenter.entity.ConfigDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ConfigDO, Long> {
}
