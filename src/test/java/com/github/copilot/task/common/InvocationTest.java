package com.github.copilot.task.common;

import com.github.copilot.util.ServiceProviderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class InvocationTest {

    private Class<?> targetClass;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] args;
    private Invocation invocation;

    @BeforeEach
    void setUp() {
        targetClass = ExampleClass.class;
        methodName = "exampleMethod";
        parameterTypes = new Class<?>[]{String.class};
        args = new Object[]{"exampleArg"};
        invocation = new Invocation(targetClass, methodName, parameterTypes, args);
    }

    @Test
    void testInvocationConstructor() {
        assertEquals(targetClass, invocation.getTargetClass());
        assertEquals(methodName, invocation.getMethodName());
        assertArrayEquals(parameterTypes, invocation.getParameterTypes());
        assertArrayEquals(args, invocation.getArgs());
    }

    @Test
    void testInvokeSuccess() throws Exception {
        try (MockedStatic<ServiceProviderUtil> mocked = mockStatic(ServiceProviderUtil.class)) {
            ExampleClass mockExampleClass = Mockito.mock(ExampleClass.class);
            mocked.when(() -> ServiceProviderUtil.getBean(targetClass)).thenReturn(mockExampleClass);

            Method method = ExampleClass.class.getMethod(methodName, String.class);
            Mockito.when(mockExampleClass.exampleMethod("exampleArg")).thenReturn("Success");

            Object result = invocation.invoke();

            assertEquals("Success", result);
        }
    }

    @Test
    void testInvokeWithNoSuchBeanDefinitionException() throws Exception {
        try (MockedStatic<ServiceProviderUtil> mocked = mockStatic(ServiceProviderUtil.class)) {
            mocked.when(() -> ServiceProviderUtil.getBean(targetClass)).thenThrow(new NoSuchBeanDefinitionException("No bean found"));

            assertThrows(Exception.class, () -> invocation.invoke());
        }
    }

    @Test
    void testInvokeException() throws Exception {
        invocation = new Invocation(targetClass, "nonExistentMethod", parameterTypes, args);
        assertThrows(Exception.class, () -> invocation.invoke());
    }

    // Example class used for mocking
    static class ExampleClass {
        public String exampleMethod(String arg) {
            return "Success";
        }
    }
}