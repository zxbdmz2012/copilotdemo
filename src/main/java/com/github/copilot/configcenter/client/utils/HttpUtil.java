package com.github.copilot.configcenter.client.utils;


import com.github.copilot.configcenter.client.model.HttpReqBO;
import com.github.copilot.configcenter.client.model.HttpRespBO;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
public class HttpUtil {

    private HttpUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static HttpRespBO httpGet(String url) {
        return httpGet(url, null, 0);
    }

    public static HttpRespBO httpGet(String url, int retry) {
        return httpGet(url, null, retry);
    }

    public static HttpRespBO httpGet(String url, Map<String, String> header) {
        return httpGet(url, header, 0);
    }

    public static HttpRespBO httpGet(String url, Map<String, String> header, int retry) {
        HttpReqBO request = new HttpReqBO();
        request.setUrl(url);
        request.setRetry(retry);
        request.setMethod("GET");
        request.setHeader(header);
        return execute(request);
    }

    public static HttpRespBO httpPost(String url, Map<String, String> header, Map<String, String> body, int retry) {
        return httpPost(url, header, Optional.ofNullable(body)
                .map(b -> b.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("&"))).orElse(null), retry);
    }

    public static HttpRespBO httpPostJson(String url, String body) {
        return httpPostJson(url, body, 2000);
    }

    public static HttpRespBO httpPostJson(String url, String body, int readTimeout) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        return httpPost(url, header, body, readTimeout);
    }

    public static HttpRespBO httpPost(String url, Map<String, String> header, String body, int readTimeout) {
        return httpPost(url, header, body, readTimeout, 0);
    }

    public static HttpRespBO httpPost(String url, Map<String, String> header, String body, int readTimeout, int retry) {
        return httpPost(url, header, Optional.ofNullable(body).map(b ->
                b.getBytes(StandardCharsets.UTF_8)).orElse(null), readTimeout, retry);
    }

    public static HttpRespBO httpPost(String url, Map<String, String> header, byte[] body, int readTimeout, int retry) {
        HttpReqBO request = new HttpReqBO();
        request.setUrl(url);
        request.setBody(body);
        request.setRetry(retry);
        request.setMethod("POST");
        request.setHeader(header);
        request.setReadTimeout(readTimeout);
        return execute(request);
    }

    private static HttpRespBO execute(HttpReqBO request) {
        int i = 0;
        HttpRespBO respBO;
        int retry = Optional.of(request.getRetry()).filter(r -> r >= 0).orElse(0);
        do {
            respBO = doExecute(request);
        } while (!respBO.success() && i++ < retry);
        return respBO;
    }

    private static HttpRespBO doExecute(HttpReqBO request) {
        HttpRespBO httpRespBO = new HttpRespBO();
        try {
            int buffer = 10240;
            URL url = new URL(request.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(request.getConnectTimeout());
            conn.setReadTimeout(request.getReadTimeout());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);// 设置本次连接是否自动处理重定向
            conn.setRequestMethod(request.getMethod());
            Optional.ofNullable(request.getHeader())
                    .ifPresent(header -> header.forEach(conn::setRequestProperty));
            conn.connect();

            if (request.getBody() != null) {
                try (OutputStream outputStream = conn.getOutputStream()) {
                    outputStream.write(request.getBody());
                    outputStream.flush();
                }
            }
            httpRespBO.setCode(conn.getResponseCode());
            httpRespBO.setMessage(conn.getResponseMessage());
            httpRespBO.setHeaderMap(conn.getHeaderFields());
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
            log.error("http execute error", e);
            Optional.of(httpRespBO).filter(r -> r.getCode() == 0).ifPresent(c -> c.setMessage(e.getMessage()));
        }
        return httpRespBO;
    }
}
