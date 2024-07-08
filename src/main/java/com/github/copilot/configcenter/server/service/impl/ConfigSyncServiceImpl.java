package com.github.copilot.configcenter.server.service.impl;

import com.github.copilot.configcenter.server.service.ConfigService;
import com.github.copilot.configcenter.server.service.ConfigSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ConfigSyncServiceImpl implements ConfigSyncService {

    @Autowired
    private ConfigService configService;

    @Override
    public void publish(long configId) {
        consume(configId);
    }

    @Override
    public void consume(long configId) {
        configService.onChangeConfigEvent(configId);
    }
}
