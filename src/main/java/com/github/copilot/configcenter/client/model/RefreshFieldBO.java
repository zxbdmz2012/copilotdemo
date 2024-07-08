package com.github.copilot.configcenter.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshFieldBO {
    /**
     * 对象实例
     */
    private Object bean;

    /**
     * 字段
     */
    private Field field;
}
