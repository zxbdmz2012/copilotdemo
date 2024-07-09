package com.github.copilot.configcenter.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * Represents a field within a bean that should be refreshed when the configuration changes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshFieldBO {
    /**
     * The bean instance containing the field to be refreshed.
     */
    private Object bean;

    /**
     * The field within the bean that should be refreshed.
     */
    private Field field;
}