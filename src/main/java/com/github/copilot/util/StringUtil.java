package com.github.copilot.util;

/**
 * Utility class for String operations.
 */
public class StringUtil {

    /**
     * Checks if a string is null or empty.
     *
     * @param str the string to check
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isEmpty(String[] strs) {
        // Check if the array is null or has no elements
        if (strs == null || strs.length == 0) {
            return true;
        }
        // Iterate through the array to check if all elements are either null or empty
        for (String str : strs) {
            if (str != null && !str.isEmpty()) {
                return false; // Found a non-empty string, so the array is not empty
            }
        }
        return true; // All strings in the array are null or empty
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotEmpty(String[] strs) {
        return !isEmpty(strs);
    }

}