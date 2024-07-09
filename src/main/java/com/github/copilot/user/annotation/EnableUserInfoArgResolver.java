package com.github.copilot.user.annotation;

import com.github.copilot.user.config.LoginArgResolverConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable automatic injection of user information into controller methods.
 * When applied to a Spring Boot application's main class, this annotation activates
 * the configuration that supports resolving and injecting user information based on the
 * current session or request context. It leverages the {@link LoginArgResolverConfig} class
 * to configure necessary beans and argument resolvers for handling user information.
 *
 * Usage of this annotation simplifies the process of accessing user information in
 * controller methods, promoting cleaner and more maintainable code.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LoginArgResolverConfig.class)
public @interface EnableUserInfoArgResolver {
}