package com.github.copilot.schedule.serializer;

/**
 * jdk序列化抽象接口
 */
public interface ObjectSerializer<T> {

    byte[] serialize(T t);

    T deserialize(byte[] bytes);
}
