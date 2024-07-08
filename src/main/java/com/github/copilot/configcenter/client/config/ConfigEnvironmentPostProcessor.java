package com.github.copilot.configcenter.client.config;

import com.github.copilot.configcenter.client.ConfigCenterClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Map;


public class ConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        String configCenterUrl = environment.getProperty("config.center.url");
        ConfigCenterClient configCenterClient = ConfigCenterClient.getInstance(configCenterUrl);
        Map<String, Object> configProperty = configCenterClient.getConfigProperty();

        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource configCenter = new MapPropertySource(ConfigCenterClient.PROPERTY_SOURCE_NAME, configProperty);
        propertySources.addFirst(configCenter);
    }
}
