package com.github.copilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.github.copilot.schedule.entity", "com.github.copilot.configcenter.entity"})
@EnableJpaRepositories({"com.github.copilot.schedule.repository", "com.github.copilot.configcenter.repository"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}