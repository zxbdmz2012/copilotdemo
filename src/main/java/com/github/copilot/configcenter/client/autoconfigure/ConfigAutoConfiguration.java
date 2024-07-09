package com.github.copilot.configcenter.client.autoconfigure;

import com.github.copilot.configcenter.client.config.ConfigRefreshAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Auto-configuration class for the configuration refresh feature.
 * This class implements {@link ImportSelector}, which allows for dynamic
 * registration of beans based on certain conditions. Specifically, it
 * registers {@link ConfigRefreshAnnotationBeanPostProcessor} to enable
 * processing of {@link com.github.copilot.configcenter.client.annotation.ConfigRefresh}
 * annotations at runtime.
 *
 * The {@link Import} annotation on this class itself triggers the inclusion
 * of {@link ConfigAutoConfiguration} during the Spring context initialization,
 * ensuring that the configuration refresh feature is set up early in the application's
 * lifecycle.
 */
@Import(ConfigAutoConfiguration.class)
public class ConfigAutoConfiguration implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // Dynamically returns the ConfigRefreshAnnotationBeanPostProcessor class name
        // for registration as a bean, enabling the processing of ConfigRefresh annotations.
        return new String[]{ConfigRefreshAnnotationBeanPostProcessor.class.getName()};
    }
}