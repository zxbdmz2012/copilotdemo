package com.github.copilotdemo.server;

import java.util.HashMap;
import java.util.Map;

// ServiceProvider is a class that manages the mapping between service interface names and their corresponding service implementations.
// It is used in the RpcController to retrieve the service implementation for a given interface name.
public class ServiceProvider {
    // A map that stores the mapping between service interface names and their corresponding service implementations.
    private Map<String, Object> interfaceProvider;

    // Constructor for the ServiceProvider. It initializes the interfaceProvider map.
    public ServiceProvider(){
        this.interfaceProvider = new HashMap<>();
    }


    // This method is used to register a service implementation with the ServiceProvider.
    // It takes an object (the service implementation) as a parameter.
    // The method retrieves all interfaces implemented by the service object and adds them to the interfaceProvider map.
    public void provideServiceInterface(Object service){
        Class<?>[] interfaces = service.getClass().getInterfaces();

        for(Class clazz : interfaces){
            interfaceProvider.put(clazz.getName(),service);
        }
    }

    // This method is used to retrieve a service implementation for a given interface name.
    // It takes the interface name as a parameter and returns the corresponding service implementation object.
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

    public <T> void addService(Class<T> serviceClass, T service){
        interfaceProvider.put(serviceClass.getName(),service);
    }
}