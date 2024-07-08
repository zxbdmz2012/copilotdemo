package com.github.copilot.configcenter.client.model;

import lombok.Data;

import java.util.Map;
import java.util.function.Predicate;


@Data
public class HttpReqBO {
    /**
     * 地址
     */
    private String url;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 请求头
     */
    private Map<String, String> header;

    /**
     * 请求体
     */
    private byte[] body;

    /**
     * 连接超时（ms）
     */
    private int connectTimeout = 2000;

    /**
     * 读取超时（ms）
     */
    private int readTimeout = 2000;

    /**
     * 重试次数
     */
    private int retry;

    /**
     * 响应成功的判断方法
     */
    private Predicate<HttpRespBO> respPredicate = HttpRespBO::success;
}
