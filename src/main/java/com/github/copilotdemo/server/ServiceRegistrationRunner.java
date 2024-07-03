package com.github.copilotdemo.server;

import com.github.copilotdemo.common.RegisterService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

// ServiceRegistrationRunner is a class that implements the ApplicationRunner interface.
// It is marked as a Spring component, so it will be automatically picked up by Spring Boot and run after the application context is loaded.
// Its main purpose is to register all Spring beans with the ServiceProvider at startup.
@Component
public class ServiceRegistrationRunner implements ApplicationRunner {

    // A reference to the ListableBeanFactory, which allows us to get all the beans in the application context.
    private final ListableBeanFactory beanFactory;
    // A reference to the ServiceProvider, which is used to register the services.
    private final ServiceProvider serviceProvider;

    // The constructor takes a ListableBeanFactory and a ServiceProvider as parameters.
    // These are automatically injected by Spring.
    public ServiceRegistrationRunner(ListableBeanFactory beanFactory, ServiceProvider serviceProvider) {
        this.beanFactory = beanFactory;
        this.serviceProvider = serviceProvider;
    }

    // The run method is called after the application context is loaded.
    // It gets all the bean names from the bean factory, retrieves each bean, and registers it with the service provider.
    @Override
    public void run(ApplicationArguments args) {
        // Get all the bean definition names from the bean factory
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        // Iterate over each bean name
        for (String beanName : beanNames) {
            // Get the bean instance from the bean factory
            Object bean = beanFactory.getBean(beanName);
            // Get the class of the bean instance
            Class<?> beanClass = bean.getClass();
            // Check if the bean class has the RegisterService annotation
            if (beanClass.isAnnotationPresent(RegisterService.class)) {
                // If it does, add the bean to the service provider
                serviceProvider.addService(bean);
            } else {
                // If it doesn't, get all the methods of the bean class
                Method[] methods = beanClass.getMethods();
                // Iterate over each method
                for (Method method : methods) {
                    // Check if the method has the RegisterService annotation
                    if (method.isAnnotationPresent(RegisterService.class)) {
                        // If it does, add the bean to the service provider and break the loop
                        serviceProvider.addService(bean);
                        break;
                    }
                }
            }
        }
    }
}