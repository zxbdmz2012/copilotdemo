package com.github.copilot.configcenter.client.annotation;

import java.lang.annotation.*;

/**
 * Annotation to mark a field or a class for automatic configuration refresh.
 * When applied, it indicates that the annotated element should be considered
 * for dynamic updates if the underlying configuration changes.
 *
 * Can be applied to:
 * - Fields: To indicate that the specific field can be dynamically updated.
 * - Types (classes, interfaces, etc.): To indicate that all eligible fields
 *   within the type may be subject to dynamic updates.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigRefresh {
}

