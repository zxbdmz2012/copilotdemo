package com.github.copilot.schedule.common;

import com.github.copilot.util.SpringContextUtil;
import lombok.Data;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Represents a method invocation, including the target class, method name, parameter types, and arguments.
 * This class is designed to be serializable so that method invocations can be saved in a database for later execution.
 * It utilizes Spring's context to fetch bean instances or falls back to reflection for instantiation if the bean is not found.
 */
@Data
public class Invocation implements Serializable {

    // The class of the target object on which the method should be invoked.
    private Class targetClass;

    // The name of the method to be invoked.
    private String methodName;

    // The types of the parameters that the method accepts.
    private Class[] parameterTypes;

    // The arguments to be passed to the method during invocation.
    private Object[] args;

    /**
     * Constructs a new Invocation with the specified target class, method name, parameter types, and arguments.
     *
     * @param targetClass    The class of the target object.
     * @param methodName     The name of the method to invoke.
     * @param parameterTypes The types of the parameters accepted by the method.
     * @param args           The arguments to pass to the method.
     */
    public Invocation(Class targetClass, String methodName, Class[] parameterTypes, Object... args) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.targetClass = targetClass;
        this.args = args;
    }

    /**
     * Invokes the specified method on the target class with the provided arguments.
     * Attempts to fetch the target bean from the Spring context; if not found, it tries to instantiate the class using reflection.
     *
     * @return The result of the method invocation.
     * @throws Exception if the method cannot be invoked for any reason (e.g., no such method, illegal access, instantiation failure, etc.).
     */
    public Object invoke() throws Exception {
        Object target = null;
        try {
            // Attempt to get the bean from Spring's application context
            target = SpringContextUtil.getBean(targetClass);
        } catch (NoSuchBeanDefinitionException e) {
            // If the bean is not found, fall back to reflection for instantiation
            target = Class.forName(targetClass.getName());
        }
        // Retrieve the method to be invoked
        Method method = target.getClass().getMethod(methodName, parameterTypes);
        // Invoke the method with the provided arguments
        return method.invoke(targetClass.newInstance(), args);
    }
}