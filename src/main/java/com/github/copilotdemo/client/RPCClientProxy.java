package com.github.copilotdemo.client;

import com.github.copilotdemo.common.RPCRequest;
import com.github.copilotdemo.common.RPCResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

@Component
// This class is responsible for creating a proxy for the remote service and handling method invocations
public class RPCClientProxy implements InvocationHandler {


    // Add a RestClient instance field
    private RestClient restClient;

    // Add a constructor to set the RestClient
    public RPCClientProxy(RestClient restClient) {
        this.restClient = restClient;
    }
    // These are the methods for each service, loaded from the application configuration
    @Value("#{${serviceMethods}}")
    private Map<String, List<String>> serviceUrlMethods;

    // These are the URLs for each service, loaded from the application configuration
    @Value("#{${services}}")
    private Map<String, String> serviceUrls;

    // This method is called when a method on the proxy object is invoked
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String url = null;

        // Get the ServiceUrl annotation from the method
        Service service = method.getAnnotation(Service.class);
        // If the annotation exists, get the url value from the annotation
        if (service != null) {
            url = service.url();
        }
        if(StringUtils.isEmpty(url)){
            // Check each service's methods to find the correct URL
            for (Map.Entry<String, List<String>> entry : serviceUrlMethods.entrySet()) {
                if (entry.getValue().contains(method.getName())) {
                    url = serviceUrls.get(entry.getKey());
                    break;
                }
            }
        }

        // If no URL was found, throw an exception
        if (url == null) {
            throw new RuntimeException("No url found for method: " + method.getName());
        }
        // Create the RPC request object
        RPCRequest request = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsTypes(method.getParameterTypes())
                .build();

        // Send the RPC request and get the response
        RPCResponse response = restClient.sendRequest(url, "api/common", request);

        // Return the data from the response
        return response.getData();
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