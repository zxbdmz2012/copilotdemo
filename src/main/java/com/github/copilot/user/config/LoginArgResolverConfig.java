package com.github.copilot.user.config;

import com.github.copilot.user.feign.UserQueryInterface;
import com.github.copilot.user.interceptor.ContextHandlerInterceptor;
import com.github.copilot.user.resolver.ContextArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Lazy
    @Autowired
    private UserQueryInterface userQueryInterface;

    /**
     * Token参数解析
     *
     * @param argumentResolvers 解析类
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ContextArgumentResolver(userQueryInterface));
    }

    /**
     * 注册 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (getHandlerInterceptor() != null) {
            String[] commonPathPatterns = getExcludeCommonPathPatterns();
            registry.addInterceptor(getHandlerInterceptor())
                    .addPathPatterns("/**")
                    .order(10)
                    .excludePathPatterns(commonPathPatterns);
            WebMvcConfigurer.super.addInterceptors(registry);
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
