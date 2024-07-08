package com.github.copilot.rpc.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

interface MockService {
    // Define your service methods here
}

class MockServiceImpl implements MockService {
    // Implement your service methods here
}

class TargetServiceProviderTest {

    private ServiceProvider provider;
    private MockService service;

    @BeforeEach
    void setUp() {
        provider = new ServiceProvider();
        service = new MockServiceImpl();
    }

    @Test
    void testAddAndGetService() {
        provider.addService(MockService.class, service);
        MockService retrievedService = (MockService) provider.getService(MockService.class.getName());

        assertNotNull(retrievedService);
        assertEquals(service, retrievedService);
    }

    @Test
    void testGetServiceWithNoRegisteredService() {
        MockService retrievedService = (MockService) provider.getService(MockService.class.getName());

        assertNull(retrievedService);
    }
}