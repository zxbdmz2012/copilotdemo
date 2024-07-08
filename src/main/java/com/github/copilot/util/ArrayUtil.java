package com.github.copilot.util;

public class ArrayUtil {

    /**
     * Checks if an array is null or empty.
     *
     * @param array the array to check
     * @return true if the array is null or empty, false otherwise
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of integers is null or empty.
     *
     * @param array the array to check
     * @return true if the array is null or empty, false otherwise
     */
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array of doubles is null or empty.
     *
     * @param array the array to check
     * @return true if the array is null or empty, false otherwise
     */
    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    // Additional utility methods can be added here as needed.
}