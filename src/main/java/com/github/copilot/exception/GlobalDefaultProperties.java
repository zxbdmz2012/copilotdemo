package com.github.copilot.exception;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties class for global default settings.
 * This class is annotated with @ConfigurationProperties to bind and validate external configurations
 * defined in application properties or YAML files. It defines properties for filtering which controllers
 * or packages the global advice applies to, allowing for fine-grained control over response wrapping
 * and exception handling behavior.
 */
@ConfigurationProperties(GlobalDefaultProperties.PREFIX)
public class GlobalDefaultProperties {

    public static final String PREFIX = "dispose";

    // List of package names to be excluded from the global response advice application.
    private List<String> adviceFilterPackage = new ArrayList<>();

    // List of class names to be excluded from the global response advice application.
    private List<String> adviceFilterClass = new ArrayList<>();

    public List<String> getAdviceFilterPackage() {
        return adviceFilterPackage;
    }

    public void setAdviceFilterPackage(List<String> adviceFilterPackage) {
        this.adviceFilterPackage = adviceFilterPackage;
    }

    public List<String> getAdviceFilterClass() {
        return adviceFilterClass;
    }

    public void setAdviceFilterClass(List<String> adviceFilterClass) {
        this.adviceFilterClass = adviceFilterClass;
    }

}
