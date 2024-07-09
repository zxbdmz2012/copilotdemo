package com.github.copilot.user.config;

import com.github.copilot.user.annotation.EnableUserInfoArgResolver;
import com.github.copilot.user.feign.UserQueryInterface;
import com.github.copilot.user.interceptor.ContextHandlerInterceptor;
import com.github.copilot.user.resolver.ContextArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class for setting up argument resolvers and interceptors related to user information.
 * This class implements the {@link WebMvcConfigurer} interface to customize the configuration for web MVC.
 * It is responsible for adding custom argument resolvers and interceptors that facilitate the handling
 * of user-related data throughout the application.
 */
@Component
public class LoginArgResolverConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext applicationContext;
    @Lazy
    @Autowired
    private UserQueryInterface userQueryInterface;

    /**
     * Adds custom argument resolvers to the application context.
     * This method checks if the application is annotated with {@link EnableUserInfoArgResolver}
     * and adds a {@link ContextArgumentResolver} to the list of argument resolvers if so.
     * The {@link ContextArgumentResolver} is used to resolve user information from the request context.
     *
     * @param argumentResolvers The list of configured argument resolvers.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (isAnnotationPresent(EnableUserInfoArgResolver.class)) {
            argumentResolvers.add(new ContextArgumentResolver(userQueryInterface));
        }
    }

    /**
     * Checks if the application is annotated with a specific annotation.
     * This method iterates over all beans in the application context and checks if any
     * are annotated with the {@link EnableUserInfoArgResolver} annotation.
     *
     * @param annotationClass The annotation class to check for.
     * @return true if the annotation is present on any bean, false otherwise.
     */
    private boolean isAnnotationPresent(Class<EnableUserInfoArgResolver> annotationClass) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Class<?> beanType = applicationContext.getType(beanName);
            if (beanType != null && AnnotationUtils.findAnnotation(beanType, annotationClass) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers interceptors in the application context.
     * This method checks if the application is annotated with {@link EnableUserInfoArgResolver}
     * and adds a {@link ContextHandlerInterceptor} to the interceptor registry if so.
     * The {@link ContextHandlerInterceptor} is used to handle user context before processing requests.
     *
     * @param registry The interceptor registry.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (isAnnotationPresent(EnableUserInfoArgResolver.class)) {
            if (getHandlerInterceptor() != null) {
                String[] commonPathPatterns = getExcludeCommonPathPatterns();
                registry.addInterceptor(getHandlerInterceptor())
                        .addPathPatterns("/**")
                        .order(10)
                        .excludePathPatterns(commonPathPatterns);
                WebMvcConfigurer.super.addInterceptors(registry);
            }
        }
    }

    /**
     * Provides the {@link HandlerInterceptor} to be registered.
     * This method returns an instance of {@link ContextHandlerInterceptor},
     * which is responsible for handling user context in incoming requests.
     *
     * @return An instance of {@link ContextHandlerInterceptor}.
     */
    protected HandlerInterceptor getHandlerInterceptor() {
        return new ContextHandlerInterceptor();
    }

    /**
     * Specifies URL patterns to be excluded from the interceptor.
     * This method returns an array of URL patterns that should not be intercepted
     * by the {@link ContextHandlerInterceptor}, such as error paths and static resources.
     *
     * @return An array of URL patterns to exclude.
     */
    protected String[] getExcludeCommonPathPatterns() {
        String[] urls = {
                "/error",
                "/login",
                "/v2/api-docs",
                "/v2/api-docs-ext",
                "/swagger-resources/**",
                "/webjars/**",
                "/",
                "/csrf",
                "/META-INF/resources/**",
                "/resources/**",
                "/static/**",
                "/public/**",
                "classpath:/META-INF/resources/**",
                "classpath:/resources/**",
                "classpath:/static/**",
                "classpath:/public/**",
                "/cache/**",
                "/swagger-ui.html**",
                "/doc.html**"
        };
        return urls;
    }
}