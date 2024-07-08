package com.github.copilot.configcenter.client.annotation;

import java.lang.annotation.*;


@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigRefresh {
}
