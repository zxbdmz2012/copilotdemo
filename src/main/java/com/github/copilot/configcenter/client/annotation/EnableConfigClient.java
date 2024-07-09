package com.github.copilot.configcenter.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable the configuration client for a Spring Boot application.
 * When applied to a Spring Boot application class, it activates the configuration
 * client features, allowing the application to fetch and update its configuration
 * dynamically from a centralized configuration server.
 *
 * Usage:
 * @EnableConfigClient
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableConfigClient {
}