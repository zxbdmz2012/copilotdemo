package com.github.copilot.rpc.client;

import com.github.copilot.rpc.common.RPCRequest;
import com.github.copilot.rpc.common.RPCResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
// RestClient is a utility class that sends HTTP requests to a specified URL.
public class RestClient {
    // sendRequest is a static method that sends a POST request to a specified URL and path.
    // It takes in a URL, a path, and an RPCRequest object as parameters.
    // It returns an RPCResponse object.
    public RPCResponse sendRequest(String url, String path, RPCRequest request) {
        // Create a new RestTemplate object. This object is used to send HTTP requests.
        RestTemplate restTemplate = new RestTemplate();

        // Set up the HTTP headers for the request. In this case, we're setting the content type to JSON.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create a new HttpEntity object. This object represents the body of the HTTP request.
        // We're passing in the RPCRequest object (which will be converted to JSON) and the headers we just set up.
        HttpEntity<RPCRequest> entity = new HttpEntity<>(request, headers);

        // Send the HTTP request and get the response. We're sending a POST request to the specified URL and path,
        // and we're passing in the HttpEntity object we just created. The response is expected to be of type RPCResponse.
        RPCResponse response = restTemplate.postForObject(url + "/" + path, entity, RPCResponse.class);

        // Return the response.
        return response;
    }
}