package com.github.copilotdemo.server;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// ServiceProvider is a class that manages the services in the application.
// It maintains a map where the keys are the names of the service interfaces and the values are the service instances.
@Component
public class ServiceProvider {
    // A map to store the service instances. The key is the name of the service interface, and the value is the service instance.
    private Map<String, Object> interfaceProvider;
    // A logger to log warning messages.
    private static final Logger LOGGER = Logger.getLogger(ServiceProvider.class.getName());

    // Constructor for the ServiceProvider. It initializes the interfaceProvider map.
    public ServiceProvider(){
        this.interfaceProvider = new HashMap<>();
    }

    // This method adds a service to the interfaceProvider map.
    // It takes the service class and the service instance as parameters.
    // It registers all interfaces implemented by the service instance, as well as the service class itself.
    public void addService(Class serviceClass, Object serviceInstance) {
        // Register all interfaces implemented by the service object
        Class<?>[] interfaces = serviceInstance.getClass().getInterfaces();
        if (interfaces.length == 0) {
            LOGGER.warning("The service instance does not implement any interfaces: " + serviceInstance.getClass().getName());
            return;
        }
        for(Class clazz : interfaces){
            interfaceProvider.put(clazz.getName(),serviceInstance);
        }
        // Also register the service class itself
        interfaceProvider.put(serviceClass.getName(),serviceInstance);
    }

    // This method adds a service to the interfaceProvider map.
    // It takes the service instance as a parameter.
    // It calls the addService method with the service class and the service instance as parameters.
    public void addService(Object serviceInstance) {
        addService(serviceInstance.getClass(), serviceInstance);
    }

    // This method retrieves a service from the interfaceProvider map.
    // It takes the name of the service interface as a parameter.
    // It returns the service instance if it exists, or null if it doesn't.
    public Object getService(String interfaceName){
        Object service = interfaceProvider.get(interfaceName);
        if (service == null) {
            LOGGER.warning("No service registered for interface: " + interfaceName);
        }
        return service;
    }
}