package com.github.copilot.configcenter.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Field;

/**
 * Extends RefreshFieldBO to include the value to which the field should be updated during a refresh.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RefreshFieldValueBO extends RefreshFieldBO {
    /**
     * The new value to be set for the field during a refresh.
     */
    private String value;

    /**
     * Constructs a RefreshFieldValueBO with the specified bean, field, and new value.
     * @param bean The bean instance containing the field.
     * @param field The field to be refreshed.
     * @param value The new value for the field.
     */
    public RefreshFieldValueBO(Object bean, Field field, String value) {
        super(bean, field);
        this.value = value;
    }
}