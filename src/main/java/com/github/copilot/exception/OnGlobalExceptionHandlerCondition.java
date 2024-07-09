package com.github.copilot.exception;

import com.github.copilot.exception.annotation.EnableGlobalExceptionHandler;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition class to check if global exception handling is enabled.
 * This class implements Spring's Condition interface to conditionally enable beans based on the presence
 * of the @EnableGlobalExceptionHandler annotation on the application's main class. It is used in conjunction
 * with the @Conditional annotation to provide conditional configuration in Spring.
 */
public class OnGlobalExceptionHandlerCondition implements Condition {

    /**
     * Checks if the application's main class is annotated with @EnableGlobalExceptionHandler.
     * This method attempts to load the main class specified in the application's properties and checks
     * if it is annotated with @EnableGlobalExceptionHandler. This determines whether global exception
     * handling should be enabled.
     *
     * @param context The condition context, providing access to the environment, bean factory, and other information.
     * @param metadata Metadata of the annotated type that the condition is being evaluated for.
     * @return true if the main class is annotated with @EnableGlobalExceptionHandler, false otherwise.
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            // Attempt to retrieve the main application class from the environment properties
            String className = context.getEnvironment().getProperty("spring.main.application-class");
            if (className != null) {
                // Load the main class
                Class<?> clazz = Class.forName(className);
                // Check if the main class is annotated with @EnableGlobalExceptionHandler
                return clazz.isAnnotationPresent(EnableGlobalExceptionHandler.class);
            }
        } catch (ClassNotFoundException e) {
            // Main class not found, condition does not match
        }
        return false;
    }
}