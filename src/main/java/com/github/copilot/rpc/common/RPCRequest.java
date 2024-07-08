package com.github.copilot.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// RPCRequest is a class that represents a request for a Remote Procedure Call (RPC).
// It implements the Serializable interface, which means it can be converted to a byte stream and sent over the network.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RPCRequest implements Serializable {
    // The name of the service interface. On the client side, only the interface name is known.
    // On the server side, the interface name is used to point to the implementation class.
    private String interfaceName;

    // The name of the method to be invoked on the remote service.
    private String methodName;

    // The parameters to be passed to the method. This is an array of objects.
    private Object[] params;

    // The types of the parameters. This is used on the server side to determine the method to be invoked.
    private Class<?>[] paramsTypes;


}