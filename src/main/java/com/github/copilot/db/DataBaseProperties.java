package com.github.copilot.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.client")
public class DataBaseProperties {

    private String url;

    private String username;

    private String diverClassName;

    private String password;

}