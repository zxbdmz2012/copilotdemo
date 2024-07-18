package com.github.copilot.rpc.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.copilot.rpc.common.RPCRequest;
import com.github.copilot.rpc.common.RPCResponse;
import com.github.copilot.rpc.common.ServiceProperties;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

@Component
// This class is responsible for creating a proxy for the remote service and handling method invocations
public class RPCClientProxy implements InvocationHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestClient restClient;
    private final ServiceProperties serviceProperties;

    public RPCClientProxy(RestClient restClient, ServiceProperties serviceProperties) {
        this.restClient = restClient;
        this.serviceProperties = serviceProperties;
    }
    // These are the methods for each service, loaded from the application configuration


    // This method is called when a method on the proxy object is invoked
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String url = null;

        // Get the ServiceUrl annotation from the method
        TargetService targetService = method.getAnnotation(TargetService.class);
        // If the annotation exists, get the url value from the annotation
        if (targetService != null) {
            url = serviceProperties.getUrls().get(targetService.url());
        }
        if (url == null || url.trim().isEmpty()) {
            for (Map.Entry<String, List<String>> entry : serviceProperties.getMethods().entrySet()) {
                if (entry.getValue().contains(method.getName())) {
                    url = serviceProperties.getUrls().get(entry.getKey());
                    break;
                }
            }
        }

        // If no URL was found, throw an exceptionhandler
        if (url == null) {
            throw new RuntimeException("No url found for method: " + method.getName());
        }
        // Create the RPC request object
        RPCRequest request = new RPCRequest(
                method.getDeclaringClass().getName(),
                method.getName(),
                args,
                method.getParameterTypes()
        );

        // Send the RPC request and get the response
        RPCResponse response = restClient.sendRequest(url, "rpc", request);

        // Return the data from the response
        if (response.isSuccess()) {
            JavaType returnType = objectMapper.getTypeFactory().constructType(method.getGenericReturnType());
            return objectMapper.convertValue(response.getResult(), returnType);
        } else {
            throw new RuntimeException("RPC call failed");
        }
    }

    // This method is used to create a proxy instance for a given class.
    public <T> T getProxy(Class<T> clazz) {
        // The Proxy.newProxyInstance method is used to create a new proxy instance.
        // The first argument is the class loader of the original class.
        // The second argument is an array of interfaces that the proxy class should implement.
        // The third argument is the invocation handler that handles method invocations on the proxy instance.
        Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);

        // The proxy instance is then cast to the original class type and returned.
        return clazz.cast(proxy);
    }
}