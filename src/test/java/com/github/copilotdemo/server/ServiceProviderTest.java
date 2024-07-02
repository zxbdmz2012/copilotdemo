package com.github.copilotdemo.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


 interface TestService {
    // Define your service methods here
}

 class TestServiceImpl implements TestService {
    // Implement your service methods here
}

class ServiceProviderTest {

    @Test
    void testAddAndGetService() {
        ServiceProvider provider = new ServiceProvider();
        TestService service = new TestServiceImpl();

        provider.addService(TestService.class, service);
        TestService retrievedService = (TestService) provider.getService(TestService.class.getName());

        assertNotNull(retrievedService);
        assertEquals(service, retrievedService);
    }
}