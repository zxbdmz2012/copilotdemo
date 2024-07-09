package com.github.copilot.configcenter.client.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Represents the response of an HTTP request, including headers, status code, message, and body.
 */
@Data
public class HttpRespBO {
    /**
     * A map of response header fields and their values.
     */
    Map<String, List<String>> headerMap;
    /**
     * The HTTP status code of the response.
     */
    private int code;
    /**
     * The HTTP status message of the response.
     */
    private String message;
    /**
     * The body of the response as a byte array.
     */
    private byte[] body;

    /**
     * Checks if the response status code is 200, indicating success.
     * @return true if the code is 200, false otherwise.
     */
    public boolean success() {
        return code == 200;
    }

    /**
     * Checks if the response is successful and contains a body.
     * @return true if the code is 200 and the body is not null, false otherwise.
     */
    public boolean ok() {
        return code == 200 && body != null;
    }

    /**
     * Returns the response body as a UTF-8 encoded string.
     * @return The body of the response, decoded as a UTF-8 string.
     */
    public String getUTF8Body() {
        return getBody(StandardCharsets.UTF_8);
    }

    /**
     * Decodes the response body using the specified charset.
     * @param charset The charset to use for decoding the body.
     * @return The decoded body as a string.
     */
    public String getBody(Charset charset) {
        return new String(body, charset);
    }

    /**
     * Provides a string representation of the HTTP response, including code, message, headers, and body.
     * @return A string representation of the response.
     */
    @Override
    public String toString() {
        return "code:" + code +
                ",message:" + message +
                ",header:" + JSON.toJSONString(headerMap) +
                ",body:" + getUTF8Body();
    }
}