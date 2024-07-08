package com.github.copilot.user.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取当前域中的 用户id appid 用户昵称
 * 注意： appid 通过token解析，  用户id 和 用户昵称必须在前端 通过请求头的方法传入。 否则这里无法获取
 */
public class BaseContextHandler {
    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String key, Long value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "0" : String.valueOf(value));
    }

    public static void set(String key, String value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "" : value);
    }

    public static void set(String key, Boolean value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "false" : value.toString());
    }


    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }


    public static String get(String key) {
        Map<String, String> map = getLocalMap();
        return map.getOrDefault(key, "");
    }

    public static String getUserId() {
        return get(UserContextConstants.userIdHeader);
    }


    private static String returnObjectValue(Object value) {
        return value == null ? "" : value.toString();
    }

    public static void remove() {
        if (THREAD_LOCAL != null) {
            THREAD_LOCAL.remove();
        }
    }

}
