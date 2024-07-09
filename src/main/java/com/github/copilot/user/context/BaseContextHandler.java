package com.github.copilot.user.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing context-specific information within the current thread.
 * This class provides a thread-safe way to store and retrieve context data such as user ID, app ID, and user nickname.
 * It leverages ThreadLocal to ensure that the data is isolated per thread, making it suitable for use in multi-threaded environments like web servers.
 *
 * Note: The app ID is obtained through token parsing, while the user ID and user nickname must be passed in via request headers from the frontend.
 * If these values are not provided through the headers, they cannot be retrieved by this handler.
 */
public class BaseContextHandler {
    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Sets a value in the current thread's context map.
     * Converts the Long value to a String before storing.
     *
     * @param key   The key under which the value should be stored.
     * @param value The value to store, will be converted to "0" if null.
     */
    public static void set(String key, Long value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "0" : String.valueOf(value));
    }

    /**
     * Sets a value in the current thread's context map.
     *
     * @param key   The key under which the value should be stored.
     * @param value The value to store, will be stored as an empty string if null.
     */
    public static void set(String key, String value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "" : value);
    }

    /**
     * Sets a Boolean value in the current thread's context map.
     * Converts the Boolean value to a String before storing.
     *
     * @param key   The key under which the value should be stored.
     * @param value The value to store, will be converted to "false" if null.
     */
    public static void set(String key, Boolean value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "false" : value.toString());
    }

    /**
     * Retrieves the current thread's context map, creating a new one if it doesn't exist.
     *
     * @return The current thread's context map.
     */
    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    /**
     * Retrieves a value from the current thread's context map based on the given key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The value associated with the specified key, or an empty string if the key doesn't exist.
     */
    public static String get(String key) {
        Map<String, String> map = getLocalMap();
        return map.getOrDefault(key, "");
    }

    /**
     * Retrieves the user ID from the current thread's context map.
     *
     * @return The user ID, or an empty string if it's not set.
     */
    public static String getUserId() {
        return get(UserContextConstants.userIdHeader);
    }

    /**
     * Removes the current thread's context map to prevent memory leaks.
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}