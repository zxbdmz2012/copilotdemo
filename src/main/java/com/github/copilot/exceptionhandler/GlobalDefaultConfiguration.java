package com.github.copilot.exceptionhandler;

import com.github.copilot.exceptionhandler.advice.CommonResponseDataAdvice;
import com.github.copilot.exceptionhandler.exception.GlobalDefaultExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;

/**
 * Configuration class to set up global exceptionhandler handling and response data advice.
 * This class defines beans for handling exceptions globally across the application
 * and for ensuring consistent response data formats. It leverages Spring's @Configuration
 * annotation to mark it as a source of bean definitions. The @EnableConfigurationProperties
 * annotation is used to make GlobalDefaultProperties available in the Spring context,
 * allowing for external configuration.
 */
@Configuration
@EnableConfigurationProperties(GlobalDefaultProperties.class)
public class GlobalDefaultConfiguration {

    /**
     * Defines a bean for global exceptionhandler handling.
     * This bean is conditionally created based on the OnGlobalExceptionHandlerCondition,
     * allowing for flexible enablement of global exceptionhandler handling.
     *
     * @return An instance of GlobalDefaultExceptionHandler.
     */
    @Bean
    @Conditional(OnGlobalExceptionHandlerCondition.class)
    public GlobalDefaultExceptionHandler globalDefaultExceptionHandler() {
        return new GlobalDefaultExceptionHandler();
    }

    /**
     * Defines a bean for db response data advice.
     * This bean is responsible for ensuring that responses from all controllers
     * adhere to a db format. It is conditionally created based on the
     * OnGlobalExceptionHandlerCondition, providing flexibility in its application.
     *
     * @param globalDefaultProperties The global default properties to configure the advice.
     * @return An instance of CommonResponseDataAdvice.
     */
    @Bean
    @Conditional(OnGlobalExceptionHandlerCondition.class)
    public CommonResponseDataAdvice commonResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties) {
        return new CommonResponseDataAdvice(globalDefaultProperties);
    }
}