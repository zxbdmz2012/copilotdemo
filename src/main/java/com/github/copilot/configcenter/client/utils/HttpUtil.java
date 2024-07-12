package com.github.copilot.configcenter.client.utils;

// Importing logging capabilities
import com.github.copilot.configcenter.client.model.HttpReqBO;
import com.github.copilot.configcenter.client.model.HttpRespBO;
import lombok.extern.slf4j.Slf4j;
// Importing URL and HttpURLConnection for making HTTP requests
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.net.URL;
// Importing OutputStream for sending request bodies
import java.io.OutputStream;
// Importing InputStream and ByteArrayOutputStream for handling response bodies
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
// Importing Map for handling headers and body parameters
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
// Importing Optional for null-safe operations
import java.util.Objects;
import java.util.Optional;
// Importing Collectors for joining map entries into a string
import java.util.stream.Collectors;
// Importing StandardCharsets for specifying character encoding
import java.nio.charset.StandardCharsets;

// Annotating the class with @Slf4j to enable logging
@Slf4j
public class HttpUtil {

    static {
        SSLContext sslContext = null;
        try {
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager},null);
        } catch (Exception e){
            log.error("init ssl context error",e);
        }
        if(Objects.nonNull(sslContext)){
            SSLContext.setDefault(sslContext);
        }
    }

    // Private constructor to prevent instantiation of this utility class
    private HttpUtil() {
        throw new IllegalStateException("Utility class");
    }

    // Overloaded httpGet method without headers and retry parameters
    public static HttpRespBO httpGet(String url) {
        return httpGet(url, null, 0);
    }

    // Overloaded httpGet method with retry parameter
    public static HttpRespBO httpGet(String url, int retry) {
        return httpGet(url, null, retry);
    }

    // Overloaded httpGet method with headers parameter
    public static HttpRespBO httpGet(String url, Map<String, String> header) {
        return httpGet(url, header, 0);
    }

    // Main httpGet method with url, headers, and retry parameters
    public static HttpRespBO httpGet(String url, Map<String, String> header, int retry) {
        // Creating a new HttpReqBO object for the request
        HttpReqBO request = new HttpReqBO();
        // Setting the request URL
        request.setUrl(url);
        // Setting the retry count
        request.setRetry(retry);
        // Setting the HTTP method to GET
        request.setMethod("GET");
        // Setting the request headers
        request.setHeader(header);
        // Executing the request and returning the response
        return execute(request);
    }

    // Overloaded httpPost method for form data with retry parameter
    public static HttpRespBO httpPost(String url, Map<String, String> header, Map<String, String> body, int retry) {
        // Converting the body map to a URL-encoded string and calling the main httpPost method
        return httpPost(url, header, Optional.ofNullable(body)
                .map(b -> b.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("&"))).orElse(null), retry);
    }

    // Overloaded httpPostJson method without readTimeout parameter
    public static HttpRespBO httpPostJson(String url, String body) {
        return httpPostJson(url, body, 2000);
    }

    public static HttpRespBO httpPostJson(String url, String body,Map<String,String> headerMap) {
        return httpPostJson(url, body, 2000,headerMap);
    }

    // Main httpPostJson method with url, body, and readTimeout parameters
    public static HttpRespBO httpPostJson(String url, String body, int readTimeout) {
        // Creating a headers map and setting Content-Type to application/json
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        // Calling the main httpPost method with the JSON body
        return httpPost(url, header, body, readTimeout);
    }

    // Main httpPostJson method with url, body, and readTimeout parameters
    public static HttpRespBO httpPostJson(String url, String body, int readTimeout,Map<String,String> headerMap) {
        // Creating a headers map and setting Content-Type to application/json
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        if(Objects.nonNull(headerMap)){
            header.putAll(headerMap);
        }
        // Calling the main httpPost method with the JSON body
        return httpPost(url, header, body, readTimeout);
    }

    // Overloaded httpPost method without retry parameter
    public static HttpRespBO httpPost(String url, Map<String, String> header, String body, int readTimeout) {
        return httpPost(url, header, body, readTimeout, 0);
    }

    // Main httpPost method with url, headers, body as a string, readTimeout, and retry parameters
    public static HttpRespBO httpPost(String url, Map<String, String> header, String body, int readTimeout, int retry) {
        // Converting the body string to bytes and calling the main httpPost method
        return httpPost(url, header, Optional.ofNullable(body).map(b ->
                b.getBytes(StandardCharsets.UTF_8)).orElse(null), readTimeout, retry);
    }

    // Main httpPost method with url, headers, body as bytes, readTimeout, and retry parameters
    public static HttpRespBO httpPost(String url, Map<String, String> header, byte[] body, int readTimeout, int retry) {
        // Creating a new HttpReqBO object for the request
        HttpReqBO request = new HttpReqBO();
        // Setting the request URL
        request.setUrl(url);
        // Setting the request body
        request.setBody(body);
        // Setting the retry count
        request.setRetry(retry);
        // Setting the HTTP method to POST
        request.setMethod("POST");
        // Setting the request headers
        request.setHeader(header);
        // Setting the read timeout
        request.setReadTimeout(readTimeout);
        // Executing the request and returning the response
        return execute(request);
    }

    // Method to execute the HTTP request and handle retries
    private static HttpRespBO execute(HttpReqBO request) {
        // Initializing retry count and response object
        int i = 0;
        HttpRespBO respBO;
        // Ensuring retry count is non-negative
        int retry = Optional.of(request.getRetry()).filter(r -> r >= 0).orElse(0);
        do {
            // Executing the request
            respBO = doExecute(request);
        } while (!respBO.success() && i++ < retry);
        // Returning the final response
        return respBO;
    }

    // Method to perform the actual HTTP request execution
    private static HttpRespBO doExecute(HttpReqBO request) {
        // Initializing the response object
        HttpRespBO httpRespBO = new HttpRespBO();
        try {
            // Setting buffer size for reading response body
            int buffer = 10240;
            // Creating URL and HttpURLConnection objects
            URL url = new URL(request.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Configuring connection properties
            conn.setConnectTimeout(request.getConnectTimeout());
            conn.setReadTimeout(request.getReadTimeout());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true); // Automatically handle redirects
            conn.setRequestMethod(request.getMethod());
            // Setting request headers
            Optional.ofNullable(request.getHeader())
                    .ifPresent(header -> header.forEach(conn::setRequestProperty));
            // Establishing the connection
            conn.connect();

            // Sending the request body if present
            if (request.getBody() != null) {
                try (OutputStream outputStream = conn.getOutputStream()) {
                    outputStream.write(request.getBody());
                    outputStream.flush();
                }
            }
            // Reading the response code, message, and headers
            httpRespBO.setCode(conn.getResponseCode());
            httpRespBO.setMessage(conn.getResponseMessage());
            httpRespBO.setHeaderMap(conn.getHeaderFields());
            // Reading the response body
            try (InputStream inputStream = conn.getInputStream();
                 ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream()) {
                int n;
                byte[] body = new byte[buffer];
                while ((n = inputStream.read(body, 0, buffer)) != -1) {
                    baOutputStream.write(body, 0, n);
                }
                httpRespBO.setBody(baOutputStream.toByteArray());
            }
        } catch (Exception e) {
            // Logging any errors and setting the error message in the response
            log.error("http execute error", e);
            Optional.of(httpRespBO).filter(r -> r.getCode() == 0).ifPresent(c -> c.setMessage(e.getMessage()));
        }
        // Returning the response object
        return httpRespBO;
    }
}