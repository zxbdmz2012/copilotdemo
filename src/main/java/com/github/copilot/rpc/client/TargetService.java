package com.github.copilot.rpc.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Service is a custom annotation used to specify the URL of the remote service for a method.
// It is used in the RPCClientProxy class to determine the URL to send the RPC request to.
@Target({ElementType.METHOD}) // This annotation can be applied to methods.
@Retention(RetentionPolicy.RUNTIME) // This annotation is available at runtime.
public @interface TargetService {
    // url is an element of the Service annotation.
    // It represents the URL of the remote service.
    // It has a default value of an empty string.
    String url() default "";
}