package com.github.copilot.configcenter.client.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Data
public class HttpRespBO {
    /**
     * 响应头
     */
    Map<String, List<String>> headerMap;
    /**
     * 响应码
     */
    private int code;
    /**
     * 响应描述
     */
    private String message;
    /**
     * 响应体
     */
    private byte[] body;

    public boolean success() {
        return code == 200;
    }

    public boolean ok() {
        return code == 200 && body != null;
    }

    public String getUTF8Body() {
        return getBody(StandardCharsets.UTF_8);
    }

    public String getBody(Charset charset) {
        return new String(body, charset);
    }

    @Override
    public String toString() {
        return "code:" + code +
                ",message:" + message +
                ",header:" + JSON.toJSONString(headerMap) +
                ",body:" + getUTF8Body();
    }
}
