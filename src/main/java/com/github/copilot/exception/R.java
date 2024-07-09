package com.github.copilot.exception;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.copilot.exception.exception.error.CommonErrorCode;

import java.io.Serializable;

/**
 * 返回统一数据结构
 */
public class R<T> implements Serializable {

    /**
     * 是否成功
     */
    private Boolean succ;


    private Long id;

    /**
     * 成功数据
     */
    private T data;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误描述
     */
    private String msg;

    public R() {
    }

    public R(Boolean succ, Long id, T data, String code, String msg) {
        this.succ = succ;
        this.id = id;
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public static R ofSuccess() {
        R r = new R();
        r.succ = true;

        return r;
    }

    public static R ofSuccess(Object data) {
        R r = new R();
        r.succ = true;
        r.setData(data);
        return r;
    }

    public static R ofFail(String code, String msg, Long id) {
        R r = new R();
        r.succ = false;
        r.code = code;
        r.msg = msg;
        r.id = id;
        return r;
    }

    public static R ofFail(String code, String msg, Object data, Long id) {
        R r = new R();
        r.succ = false;
        r.code = code;
        r.msg = msg;
        r.setData(data);
        r.id = id;
        return r;
    }

    public static R ofFail(CommonErrorCode resultEnum, Long id) {
        R r = new R();
        r.id = id;
        r.succ = false;
        r.code = resultEnum.getCode();
        r.msg = resultEnum.getMessage();
        return r;
    }

    public Boolean getSucc() {
        return succ;
    }

    public void setSucc(Boolean succ) {
        this.succ = succ;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取 json
     */
    public String buildResultJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("succ", this.succ);
        jsonObject.put("code", this.code);
        jsonObject.put("id", this.id);
        jsonObject.put("msg", this.msg);
        jsonObject.put("data", this.data);
        return JSON.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public String toString() {
        return "Result{" +
                "succ=" + succ +
                ", id=" + id +
                ", data=" + data +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
