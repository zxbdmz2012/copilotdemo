package com.github.copilot.rpc.client;

import com.github.copilot.rpc.common.RPCRequest;
import com.github.copilot.rpc.common.RPCResponse;
import com.github.copilot.rpc.common.ServiceProperties;
import com.github.copilot.rpc.sample.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RPCClientProxyTest {

    @InjectMocks
    private RPCClientProxy rpcClientProxy;

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceProperties serviceProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Map<String, String> serviceUrls = new HashMap<>();
        serviceUrls.put("testService", "http://test.com");

        Map<String, List<String>> serviceUrlMethods = new HashMap<>();
        serviceUrlMethods.put("testService", Collections.singletonList("testMethod"));

        when(serviceProperties.getUrls()).thenReturn(serviceUrls);
        when(serviceProperties.getMethods()).thenReturn(serviceUrlMethods);
    }

    @Test
    void testInvoke() throws Throwable {
        // Create a mock Method
        Method mockMethod = UserService.class.getMethod("getUserByUserId", String.class);

        // Create a mock RPCRequest
        RPCRequest mockRequest = new RPCRequest();
        mockRequest.setInterfaceName("com.github.copilotdemo.UserService");
        mockRequest.setMethodName("getUserByUserId");
        mockRequest.setParams(new Object[]{"testUserId"});
        mockRequest.setParamsTypes(new Class[]{String.class});

        // Create the expected RPCResponse
        RPCResponse expectedResponse = new RPCResponse();
        expectedResponse.setData("testData");

        // Set the behavior of the restClient
        when(restClient.sendRequest(any(String.class), any(String.class), any(RPCRequest.class))).thenReturn(expectedResponse);

        // Call the invoke method
        Object result = rpcClientProxy.invoke(rpcClientProxy, mockMethod, new Object[]{"testUserId"});

        // Verify the result
        assertEquals(expectedResponse.getData(), result);
    }
}