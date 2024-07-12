package com.github.copilot.task.serializer;


public interface ObjectSerializer<T> {

    byte[] serialize(T t);

    T deserialize(byte[] bytes);
}
