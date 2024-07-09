package com.github.copilot;

import com.github.copilot.exception.annotation.EnableGlobalExceptionHandler;
import com.github.copilot.user.annotation.EnableUserInfoArgResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableUserInfoArgResolver
@EnableScheduling
@EnableGlobalExceptionHandler
@EntityScan(basePackages = {"com.github.copilot.schedule.entity", "com.github.copilot.configcenter.entity","com.github.copilot.exception.entity"})
@EnableJpaRepositories({"com.github.copilot.schedule.repository", "com.github.copilot.configcenter.repository","com.github.copilot.exception.repository"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}