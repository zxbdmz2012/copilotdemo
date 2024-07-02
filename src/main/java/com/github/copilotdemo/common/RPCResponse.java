package com.github.copilotdemo.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// RPCResponse is a class that represents the response from an RPC request.
// It implements the Serializable interface, which means it can be converted to a byte stream and sent over the network.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RPCResponse implements Serializable {

    // The status code of the response. A code of 200 means success, and a code of 500 means failure.
    private int code;

    // The message of the response. This is typically used to provide additional information about the response.
    private String message;

    // The data of the response. This is the actual object that is being sent in the response.
    private Object data;

    // A static method that creates a new RPCResponse object representing a successful response.
    // It takes in the data object to be sent in the response.
    public static RPCResponse success(Object data) {
        return RPCResponse.builder().code(200).data(data).build();
    }

    // A static method that creates a new RPCResponse object representing a failed response.
    public static RPCResponse fail() {
        return RPCResponse.builder().code(500).message("服务器发生错误").build();
    }

    public Object getResult() {
        return this.data;
    }

    public boolean isSuccess() {
        return this.code == 200;
    }
}