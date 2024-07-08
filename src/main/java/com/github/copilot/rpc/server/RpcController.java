package com.github.copilot.rpc.server;

import com.github.copilot.rpc.common.RPCRequest;
import com.github.copilot.rpc.common.RPCResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// RpcController is a class that handles incoming RPC requests.
// It uses the Spring @RestController annotation, which means it's a controller where every method returns a domain object instead of a view.
@RestController
@NoArgsConstructor
public class RpcController {

    @Autowired
    // A reference to the ServiceProvider, which provides the services that can be called via RPC.
    private ServiceProvider serviceProvider;

    // This method handles POST requests to the /rpc endpoint.
    // It takes an RPCRequest as a parameter, which is automatically deserialized from the request body.
    // It returns an RPCResponse, which is automatically serialized to JSON and sent as the response body.
    @PostMapping("/rpc")
    public RPCResponse handleRequest(@RequestBody RPCRequest request) {
        // Use reflection to call the requested service method and get the response.
        RPCResponse response = getResponse(request);
        return response;
    }

    // This method uses reflection to call the requested service method and get the response.
    // It takes an RPCRequest as a parameter, which contains the details of the service method to call.
    // It returns an RPCResponse, which contains the result of the service method call.
    private RPCResponse getResponse(RPCRequest request) {
        // Get the name of the service interface from the request.
        String interfaceName = request.getInterfaceName();
        // Get the service implementation object from the ServiceProvider.
        Object service = serviceProvider.getService(interfaceName);
        if (service == null) {
            System.out.println("Service is null for interface: " + interfaceName);
            return RPCResponse.fail();
        }
        // Use reflection to call the service method.
        Method method = null;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
            Object invoke = method.invoke(service, request.getParams());
            // If the method call is successful, return a success response with the result of the method call.
            return RPCResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("Method execution error");
            return RPCResponse.fail();
        }
    }
}