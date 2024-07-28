package com.github.copilot.configcenter.server.dao.impl;

import com.github.copilot.configcenter.entity.ConfigDO;
import com.github.copilot.configcenter.repository.ConfigRepository;
import com.github.copilot.configcenter.server.dao.ConfigDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
public class ConfigDAOImpl implements ConfigDAO {

    @Autowired
    private ConfigRepository configRepository;

    @Override
    public List<ConfigDO> getUpdatedConfigs() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -5);
        Date fiveSecondsAgo = calendar.getTime();
        return configRepository.findByUpdateTimeAfter(fiveSecondsAgo);
    }

    @Override
    public long insertConfigDO(ConfigDO configDO) {
        return configRepository.save(configDO).getId();
    }

    @Override
    public void updateConfig(ConfigDO configDO) {
        configRepository.save(configDO);
    }

    @Override
    public void delConfig(long id) {
        configRepository.deleteById(id);
    }

    @Override
    public ConfigDO getConfig(long id) {
        Optional<ConfigDO> optional = configRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public List<ConfigDO> getAllConfig() {
        return configRepository.findAll();
    }

}


