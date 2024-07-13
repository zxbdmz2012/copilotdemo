package com.github.copilot.exceptionhandler.advice;

import com.alibaba.fastjson.JSON;
import com.github.copilot.exceptionhandler.GlobalDefaultProperties;
import com.github.copilot.exceptionhandler.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * A controller advice that ensures all responses from controllers adhere to a standardized format.
 * This class intercepts responses before they are sent to the client, allowing for modification or wrapping
 * to ensure consistency across the application. It is particularly useful for ensuring that responses
 * from different parts of the application follow the same structure, simplifying client-side handling.
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    private final GlobalDefaultProperties globalDefaultProperties;

    /**
     * Constructs a new CommonResponseDataAdvice instance with global default properties.
     * These properties can be used to configure the behavior of the advice, such as filtering
     * which controllers or packages it applies to.
     *
     * @param globalDefaultProperties The global default properties to apply to this advice.
     */
    public CommonResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties) {
        this.globalDefaultProperties = globalDefaultProperties;
    }

    /**
     * Determines if the advice supports the given controller method.
     * This can be used to selectively apply the advice to certain responses based on the controller,
     * method annotations, or other criteria.
     *
     * @param methodParameter The method parameter of the controller method.
     * @param aClass The class of the message converter.
     * @return true if the advice should be applied to the response, false otherwise.
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        return filter(methodParameter);
    }

    /**
     * Modifies or wraps the body of the response before it is sent to the client.
     * This method is called after the controller method has executed and before the response
     * is written. It can modify the response body to ensure it follows the standardized format.
     *
     * @param o The body of the response from the controller method.
     * @param methodParameter The method parameter of the controller method.
     * @param mediaType The media type of the response.
     * @param aClass The class of the message converter.
     * @param serverHttpRequest The server HTTP request.
     * @param serverHttpResponse The server HTTP response.
     * @return The modified or wrapped response body.
     */
    @Nullable
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        // Handle null response body
        if (o == null) {
            if (methodParameter.getParameterType().getName().equals("java.lang.String")) {
                return JSON.toJSON(Result.ofSuccess()).toString();
            }
            return Result.ofSuccess();
        }
        // Directly return if the response is already in the desired format
        if (o instanceof Result) {
            return (Result<Object>) o;
        }
        // Special handling for String responses to avoid ClassCastException
        if (o instanceof String) {
            return JSON.toJSON(Result.ofSuccess(o)).toString();
        }
        // Wrap the response body in the standardized format
        return Result.ofSuccess(o);
    }

    /**
     * Filters the controller methods to which this advice applies.
     * This method checks the controller's package, class name, and method annotations
     * against configured filters to determine if the advice should be applied.
     *
     * @param methodParameter The method parameter of the controller method.
     * @return true if the advice should be applied, false if it should be skipped.
     */
    private Boolean filter(MethodParameter methodParameter) {
        Class<?> declaringClass = methodParameter.getDeclaringClass();
        // Check package filters
        long count = globalDefaultProperties.getAdviceFilterPackage().stream()
                .filter(l -> declaringClass.getName().contains(l)).count();
        if (count > 0) {
            return false;
        }
        // Check class filters
        if (globalDefaultProperties.getAdviceFilterClass().contains(declaringClass.getName())) {
            return false;
        }

        // Apply advice if the method is annotated with @Result
        return methodParameter.getMethod().isAnnotationPresent(com.github.copilot.exceptionhandler.annotation.Result.class);
    }

}