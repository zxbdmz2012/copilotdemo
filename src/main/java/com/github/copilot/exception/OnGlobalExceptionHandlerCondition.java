package com.github.copilot.exception;

import com.github.copilot.exception.annotation.EnableGlobalExceptionHandler;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnGlobalExceptionHandlerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            // 尝试获取Spring应用程序的主类
            String className = context.getEnvironment().getProperty("spring.main.application-class");
            if (className != null) {
                // 加载主类
                Class<?> clazz = Class.forName(className);
                // 检查主类是否有EnableGlobalExceptionHandler注解
                return clazz.isAnnotationPresent(EnableGlobalExceptionHandler.class);
            }
        } catch (ClassNotFoundException e) {
            // 主类未找到，条件不匹配
        }
        return false;
    }
}