package com.github.copilotdemo.common;

import com.github.copilotdemo.server.RpcController;
import com.github.copilotdemo.server.ServiceProvider;
import com.github.copilotdemo.sample.service.UserService;
import com.github.copilotdemo.sample.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyStarterAutoConfiguration {

    @Bean
    public ServiceProvider serviceProvider() {
        return new ServiceProvider();
    }

    @Bean
    public RpcController rpcController(ServiceProvider serviceProvider) {
        return new RpcController(serviceProvider);
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
}