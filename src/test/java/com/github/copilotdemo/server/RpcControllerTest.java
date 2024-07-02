package com.github.copilotdemo.server;

import com.github.copilotdemo.common.RPCRequest;
import com.github.copilotdemo.common.RPCResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RpcControllerTest {

    @InjectMocks
    private RpcController controller;

    @Mock
    private ServiceProvider serviceProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleRequest() {
        // Mock the ServiceProvider
        when(serviceProvider.getService(anyString())).thenReturn(new TestService());

        RPCRequest request = new RPCRequest();
        request.setInterfaceName("TestInterface");
        request.setMethodName("TestMethod");
        request.setParams(new Object[]{1, "test"});
        request.setParamsTypes(new Class[]{int.class, String.class});

        RPCResponse response = controller.handleRequest(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        // Add more assertions based on your expected response
    }

    // A dummy service for testing
    class TestService {
        public void TestMethod(int i, String s) {
            // Method implementation here
        }
    }
}