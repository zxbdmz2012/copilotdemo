package com.github.copilotdemo.client;

import com.github.copilotdemo.common.RPCRequest;
import com.github.copilotdemo.common.RPCResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RPCClientProxyTest {

    @InjectMocks
    private RPCClientProxy rpcClientProxy;

    @Mock
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        rpcClientProxy = new RPCClientProxy(restClient);

        MockitoAnnotations.openMocks(this);
        Map<String, String> serviceUrls = new HashMap<>();
        serviceUrls.put("testService", "http://test.com");
        ReflectionTestUtils.setField(rpcClientProxy, "serviceUrls", serviceUrls);

        Map<String, List<String>> serviceUrlMethods = new HashMap<>();
        serviceUrlMethods.put("testService", Collections.singletonList("testMethod"));
        ReflectionTestUtils.setField(rpcClientProxy, "serviceUrlMethods", serviceUrlMethods);
    }

    @Test
    void testInvoke() throws Throwable {
        RPCResponse expectedResponse = new RPCResponse();
        expectedResponse.setData("testData");
        when(restClient.sendRequest(any(String.class), any(String.class), any(RPCRequest.class)))
                .thenReturn(expectedResponse);

        Method method = TestService.class.getMethod("testMethod");
        Object result = rpcClientProxy.invoke(null, method, null);

        assertEquals("testData", result);
    }

    @Test
    void testGetProxy() {
        TestService testService = rpcClientProxy.getProxy(TestService.class);
        assertNotNull(testService);
    }

    interface TestService {
        void testMethod();
    }
}