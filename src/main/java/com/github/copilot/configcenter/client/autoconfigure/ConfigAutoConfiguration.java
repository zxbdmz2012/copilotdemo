package com.github.copilot.configcenter.client.autoconfigure;

import com.github.copilot.configcenter.client.config.ConfigRefreshAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;


@Import(ConfigAutoConfiguration.class)
public class ConfigAutoConfiguration implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{ConfigRefreshAnnotationBeanPostProcessor.class.getName()};
    }
}
