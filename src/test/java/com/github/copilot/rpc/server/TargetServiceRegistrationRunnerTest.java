package com.github.copilot.rpc.server;

import com.github.copilot.rpc.common.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TargetServiceRegistrationRunnerTest {

    private ListableBeanFactory beanFactory;
    private ServiceProvider serviceProvider;
    private ServiceRegistrationRunner serviceRegistrationRunner;

    @BeforeEach
    void setUp() {
        // Create mock objects
        beanFactory = Mockito.mock(ListableBeanFactory.class);
        serviceProvider = Mockito.mock(ServiceProvider.class);

        // Create the object to be tested
        serviceRegistrationRunner = new ServiceRegistrationRunner(beanFactory, serviceProvider);
    }

    // Create a class with the RegisterService annotation
    @RegisterService
    class MockBean {
    }

    @Test
    void run_withRegisterServiceAnnotation() {
        // Create an instance of the class with the RegisterService annotation
        Object mockBean = new MockBean();

        // When getBeanDefinitionNames is called on beanFactory, return an array with one element
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"mockBean"});

        // When getBean is called on beanFactory with "mockBean", return the mock bean
        when(beanFactory.getBean("mockBean")).thenReturn(mockBean);

        // Call the method to be tested
        serviceRegistrationRunner.run(Mockito.mock(ApplicationArguments.class));

        // Verify that addService was called on serviceProvider with the mock bean
        verify(serviceProvider, times(1)).addService(any());
    }

    @Test
    void run_withoutRegisterServiceAnnotation() {
        // Create a mock bean without the RegisterService annotation
        Object mockBean = Mockito.mock(Object.class);

        // When getBeanDefinitionNames is called on beanFactory, return an array with one element
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"mockBean"});

        // When getBean is called on beanFactory with "mockBean", return the mock bean
        when(beanFactory.getBean("mockBean")).thenReturn(mockBean);

        // Call the method to be tested
        serviceRegistrationRunner.run(Mockito.mock(ApplicationArguments.class));

        // Verify that addService was not called on serviceProvider with the mock bean
        verify(serviceProvider, times(0)).addService(mockBean);
    }
}