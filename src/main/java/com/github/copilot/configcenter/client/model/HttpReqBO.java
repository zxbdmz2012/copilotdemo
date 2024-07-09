package com.github.copilot.configcenter.client.model;

import lombok.Data;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Represents an HTTP request, including URL, method, headers, body, timeouts, retry count, and a predicate for success.
 */
@Data
public class HttpReqBO {
    /**
     * The URL to which the request will be sent.
     */
    private String url;

    /**
     * The HTTP method (e.g., GET, POST) of the request.
     */
    private String method;

    /**
     * A map of header names to values for the request.
     */
    private Map<String, String> header;

    /**
     * The body of the request, if any.
     */
    private byte[] body;

    /**
     * The timeout in milliseconds for establishing a connection.
     */
    private int connectTimeout = 2000;

    /**
     * The timeout in milliseconds for reading the response.
     */
    private int readTimeout = 2000;

    /**
     * The number of times to retry the request in case of failure.
     */
    private int retry;

    /**
     * A predicate that determines whether the response to the request is considered successful.
     */
    private Predicate<HttpRespBO> respPredicate = HttpRespBO::success;
}