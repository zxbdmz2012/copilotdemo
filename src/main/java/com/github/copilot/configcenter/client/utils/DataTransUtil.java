
package com.github.copilot.configcenter.client.utils;

// Importing necessary classes from the Spring framework and Java Collections
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

// Defines a utility class for data transformation operations
public class DataTransUtil {

    // A static method to build a flattened map from a nested structure
    public static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        // Iterating over each entry in the source map
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            // Extracting the key from the current entry
            String key = entry.getKey();
            // If a path is provided, prepend it to the key
            if (StringUtils.hasText(path)) {
                // If the key starts with a bracket, it indicates an array index; append it directly
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    // Otherwise, append the key with a dot separator
                    key = path + '.' + key;
                }
            }
            // Extracting the value from the current entry
            Object value = entry.getValue();
            // If the value is a String, add it directly to the result map
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                // If the value is a Map, recursively flatten it
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                // If the value is a Collection, process each element
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                int count = 0;
                for (Object object : collection) {
                    // For each element in the collection, recursively flatten it with an indexed key
                    buildFlattenedMap(result,
                            Collections.singletonMap("[" + (count++) + "]", object), key);
                }
            } else {
                // For any other type of value, convert it to a String and add it to the result map
                result.put(key, (value != null ? value.toString() : ""));
            }
        }
    }
}
