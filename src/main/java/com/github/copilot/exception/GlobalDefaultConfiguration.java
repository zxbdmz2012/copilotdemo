package com.github.copilot.exception;


import com.github.copilot.exception.advice.CommonResponseDataAdvice;
import com.github.copilot.exception.exception.GlobalDefaultExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.context.annotation.Conditional;

@Configuration
@EnableConfigurationProperties(GlobalDefaultProperties.class)
public class GlobalDefaultConfiguration {

    @Bean
    @Conditional(OnGlobalExceptionHandlerCondition.class)
    public GlobalDefaultExceptionHandler globalDefaultExceptionHandler() {
        return new GlobalDefaultExceptionHandler();
    }

    @Bean
    @Conditional(OnGlobalExceptionHandlerCondition.class)
    public CommonResponseDataAdvice commonResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties) {
        return new CommonResponseDataAdvice(globalDefaultProperties);
    }
}
