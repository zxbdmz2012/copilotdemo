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
 * 公共配置类, 一些公共工具配置
 */
@Component
public class LoginArgResolverConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext applicationContext;
    @Lazy
    @Autowired
    private UserQueryInterface userQueryInterface;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // Check if the startup class is annotated with @EnableUserInfoArgResolver
        if (isAnnotationPresent(EnableUserInfoArgResolver.class)) {
            argumentResolvers.add(new ContextArgumentResolver(userQueryInterface));
        }
    }

    private boolean isAnnotationPresent(Class<EnableUserInfoArgResolver> annotationClass) {
        // Iterate over all beans and check for the specified annotation
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
     * 注册 拦截器
     *
     * @param registry
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

    protected HandlerInterceptor getHandlerInterceptor() {
        return new ContextHandlerInterceptor();
    }

    /**
     * auth-client 中的拦截器需要排除拦截的地址
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
