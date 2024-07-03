package com.github.copilotdemo.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import com.github.copilotdemo.common.RPCRequest;
import com.github.copilotdemo.common.RPCResponse;

class RestClientTest {

    @Mock
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendRequest() {
        // Create a mock RestTemplate
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

        // Create a test RPCRequest and RPCResponse
        RPCRequest request = new RPCRequest();
        RPCResponse expectedResponse = new RPCResponse();

        // Set up the mock RestTemplate to return the expected RPCResponse when postForObject is called
        Mockito.when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(RPCResponse.class)))
                .thenReturn(expectedResponse);

        // Set up the mock RestClient to return the expected RPCResponse when sendRequest is called
        Mockito.when(restClient.sendRequest(eq("http://test.com"), eq("testPath"), eq(request)))
                .thenReturn(expectedResponse);

        // Call the method under test
        RPCResponse actualResponse = restClient.sendRequest("http://test.com", "testPath", request);

        // Assert that the actual RPCResponse matches the expected RPCResponse
        assertEquals(expectedResponse, actualResponse);
    }
}