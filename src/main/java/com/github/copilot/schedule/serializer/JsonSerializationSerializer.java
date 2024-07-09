package com.github.copilot.schedule.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JsonSerializationSerializer<T> implements ObjectSerializer<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(T object) {
        if (object == null) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(object);
            return json.getBytes();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            String json = new String(bytes);
            // Note: The type information is lost here; you'll need to adjust this method to handle type correctly.
            return (T) objectMapper.readValue(json, Object.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to deserialize object", e);
        }
    }
}