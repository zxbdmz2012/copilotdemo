package com.github.copilot.rpc.server;

import com.github.copilot.util.ServiceProviderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// ServiceProvider is a class that manages the services in the application.
// It maintains a map where the keys are the names of the service interfaces and the values are the service instances.
@Component
@Slf4j
public class ServiceProvider {


    public Object getService(String interfaceName) {
        Object service = null;
        try {
            service = ServiceProviderUtil.getBean(Class.forName(interfaceName));
        } catch (ClassNotFoundException e) {
            log.error( "Class not found for interface: {}", interfaceName);
        }
        if (service == null) {
            log.error( "No service registered for interface: {}", interfaceName);
        }
        return service;
    }
}