package com.github.copilot.configcenter.client.config;

import com.github.copilot.configcenter.client.ConfigCenterClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Map;

/**
 * An implementation of {@link EnvironmentPostProcessor} that integrates with the Config Center.
 * This post-processor is triggered during the startup phase of a Spring Boot application,
 * allowing it to modify the environment before the application context is refreshed.
 *
 * It fetches configuration properties from a centralized configuration server (Config Center)
 * and adds them to the Spring Environment. This enables dynamic configuration management,
 * where application settings can be updated without needing to restart the application.
 *
 * Usage:
 * It must be registered in `META-INF/spring.factories` under the
 * `org.springframework.boot.env.EnvironmentPostProcessor` key to be discovered by Spring Boot.
 */
public class ConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        // Fetch the URL of the Config Center from the environment
        String configCenterUrl = environment.getProperty("config.center.url");
        // Obtain an instance of the ConfigCenterClient using the URL
        ConfigCenterClient configCenterClient = ConfigCenterClient.getInstance(configCenterUrl);
        // Fetch configuration properties from the Config Center
        Map<String, Object> configProperty = configCenterClient.getConfigProperty();

        // Access the current list of property sources in the environment
        MutablePropertySources propertySources = environment.getPropertySources();
        // Create a new property source with the fetched configuration
        MapPropertySource configCenter = new MapPropertySource(ConfigCenterClient.PROPERTY_SOURCE_NAME, configProperty);
        // Add the new property source at the beginning of the list to give it high priority
        propertySources.addFirst(configCenter);
    }
}