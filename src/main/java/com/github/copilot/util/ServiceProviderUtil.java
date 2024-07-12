package com.github.copilot.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Utility class for accessing the Spring application context from anywhere in the application.
 * This class implements the ApplicationContextAware interface to receive the ApplicationContext,
 * which is automatically injected by Spring during the initialization phase.
 */
@Component
public class ServiceProviderUtil implements ApplicationContextAware {

    // Static variable to hold the ApplicationContext
    private static ApplicationContext context;

    /**
     * Retrieves the ApplicationContext.
     *
     * @return the ApplicationContext instance
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * Overridden method from ApplicationContextAware interface to set the ApplicationContext.
     * Spring automatically calls this method, passing in the ApplicationContext to be used by this utility class.
     *
     * @param context the ApplicationContext object to be injected
     * @throws BeansException if there is an issue when setting the ApplicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ServiceProviderUtil.context = context;
    }

    /**
     * Retrieves a bean from the ApplicationContext by its name.
     *
     * @param beanName the name of the bean to retrieve
     * @return the bean instance or null if not found
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     * Retrieves a bean from the ApplicationContext by its type.
     *
     * @param clazz the class type of the bean to retrieve
     * @return the bean instance or null if not found
     */
    public static Object getBean(Class clazz) {
        return context.getBean(clazz);
    }

    /**
     * Retrieves a bean from the ApplicationContext by its type and name.
     * This method is useful when multiple beans of the same type exist and you want to get a specific bean.
     *
     * @param clazz the class type of the bean to retrieve
     * @param name  the name of the bean to retrieve
     * @param <T>   the type of the bean to return
     * @return the bean instance or null if not found
     */
    public static <T> T getByTypeAndName(Class<T> clazz, String name) {
        Map<String, T> clazzMap = context.getBeansOfType(clazz);
        return clazzMap.get(name);
    }
}
