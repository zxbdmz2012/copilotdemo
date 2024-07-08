package com.github.copilot.configcenter.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Field;


@Data
@EqualsAndHashCode(callSuper = true)
public class RefreshFieldValueBO extends RefreshFieldBO {
    /**
     * 值
     */
    private String value;

    public RefreshFieldValueBO(Object bean, Field field, String value) {
        super(bean, field);
        this.value = value;
    }
}
